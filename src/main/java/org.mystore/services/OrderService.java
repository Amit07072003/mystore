package org.mystore.services;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mystore.dtos.OrderDTO;
import org.mystore.dtos.OrderItemDTO;
import org.mystore.mapper.OrderMapper;
import org.mystore.models.*;
import org.mystore.repositories.CartRepository;
import org.mystore.repositories.OrderItemRepository;
import org.mystore.repositories.OrderRepository;
import org.mystore.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {



    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    @Transactional
    public void processSuccessfulPayment(String razorpayOrderId, Long amount, Long userId) {
        // 1️⃣ Find the order by Razorpay Order ID
        Order order = orderRepository.findByRazorpayOrderId(razorpayOrderId);
        if (order == null) {
            log.warn("Order not found for Razorpay Order ID: {}", razorpayOrderId);
            return;
        }

        // 2️⃣ Fetch user's cart
        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
        if (optionalCart.isEmpty()) {
            log.warn("No cart found for user {}", userId);
            return;
        }

        Cart cart = optionalCart.get();
        List<CartItem> cartItems = cart.getItems();
        if (cartItems.isEmpty()) {
            log.warn("Cart is empty for user {}", userId);
            return;
        }

        // 3️⃣ Create OrderItem rows and update stock
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            // Create OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice()) // snapshot price
                    .build();
            orderItemRepository.save(orderItem);

            // Update product stock
            int newQuantity = product.getStockQuantity() - cartItem.getQuantity();
            product.setStockQuantity(Math.max(newQuantity, 0));
            productRepository.save(product);

            log.info("Product {} stock updated: {}", product.getId(), product.getStockQuantity());
        }

        // 4️⃣ Update order status and timestamp
        order.setStatus("PAID");
        order.setOrderDate(LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Kolkata")));
//        order.setTotalAmount(amount); // ensure totalAmount is set
        orderRepository.save(order);

        // 5️⃣ Clear user's cart after successful payment
        cart.getItems().clear();
        cartRepository.save(cart);
        log.info("Cart cleared for user {}", userId);

        log.info("Order {} marked as PAID. OrderItems created and stock updated successfully.", order.getId());
    }

    // ✅ Fetch order by Razorpay order ID
    public Order getOrderByRazorpayOrderId(String razorpayOrderId) {
        return orderRepository.findByRazorpayOrderId(razorpayOrderId);
    }

    // ✅ Get user ID from Razorpay order ID
    public Long getUserIdByRazorpayOrderId(String razorpayOrderId) {
        Order order = orderRepository.findByRazorpayOrderId(razorpayOrderId);
        if (order == null || order.getUser() == null) {
            log.warn("Order or user not found for Razorpay Order ID: {}", razorpayOrderId);
            return null;
        }
        Long userId = order.getUser().getId();
        log.info("userId from order service: {}", userId);
        return userId;
    }

    // ✅ Get order by DB ID
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }

    // ✅ Get order amount by Razorpay reference
    public Long getOrderAmountByReferenceId(String razorpayOrderId) {
        Order order = orderRepository.findByRazorpayOrderId(razorpayOrderId);
        if (order == null) {
            throw new RuntimeException("Order not found for Razorpay Reference ID: " + razorpayOrderId);
        }
        return order.getTotalAmount();
    }



    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> {
                    OrderDTO dto = new OrderDTO();
                    dto.setId(order.getId());
                    dto.setRazorpayOrderId(order.getRazorpayOrderId());
                    dto.setOrderDate(order.getOrderDate());
                    dto.setTotalAmount(order.getTotalAmount());
                    dto.setStatus(order.getStatus());

                    // Map order items
                    if (order.getOrderItems() != null) {
                        dto.setItems(order.getOrderItems()
                                .stream()
                                .map(item -> {
                                    OrderItemDTO itemDTO = new OrderItemDTO();
                                    itemDTO.setId(item.getId());
                                    itemDTO.setProductId(item.getProduct().getId());
                                    itemDTO.setProductName(item.getProduct().getName());
                                    itemDTO.setQuantity(item.getQuantity());
                                    itemDTO.setPrice(item.getPrice());
                                    return itemDTO;
                                })
                                .collect(Collectors.toList()));
                    }

                    // Set user ID or email
                    if (order.getUser() != null) {
                        dto.setUserId(order.getUser().getId());
                        dto.setUserEmail(order.getUser().getEmail());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }


    public OrderDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        order.setStatus(status);
        orderRepository.save(order);

        // Convert to DTO
        List<OrderItemDTO> items = order.getOrderItems().stream().map(item ->
                OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build()
        ).toList();

        return OrderDTO.builder()
                .id(order.getId())
                .razorpayOrderId(order.getRazorpayOrderId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .items(items)
                .build();
    }


    // Return List<OrderDTO> for seller
    public List<OrderDTO> getOrdersForSeller(Long sellerId) {
        // Fetch orders from repository
        List<Order> orders = orderRepository.findOrdersBySellerId(sellerId);

        // Map each Order to OrderDTO
        return orders.stream()
                .map(OrderMapper::toDTO) // map single Order → OrderDTO
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersForUser(Long userId) {

        List<Order> orders = orderRepository.findOrdersByUserId(userId);

        return orders.stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }


}
