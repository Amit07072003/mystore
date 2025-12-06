package org.mystore.mapper;

import org.mystore.dtos.OrderDTO;
import org.mystore.dtos.OrderItemDTO;
import org.mystore.models.Order;
import org.mystore.models.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    // Convert Order entity to OrderDTO (all items)
    public static OrderDTO toDTO(Order order) {
        if (order == null) return null;
        return toDTO(order, order.getOrderItems());
    }

    // Convert Order entity to OrderDTO with a specific list of items (filtered)
    public static OrderDTO toDTO(Order order, List<OrderItem> items) {
        if (order == null) return null;

        List<OrderItemDTO> itemDTOs = toItemDTOList(items);

        return OrderDTO.builder()
                .id(order.getId())
                .razorpayOrderId(order.getRazorpayOrderId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .userEmail(order.getUser() != null ? order.getUser().getEmail() : null)
                .items(itemDTOs)
                .build();
    }

    // Convert List of OrderItem entities to DTOs
    public static List<OrderItemDTO> toItemDTOList(List<OrderItem> items) {
        if (items == null) return null;

        return items.stream()
                .map(item -> {
                    Long price = null;
                    if (item.getPrice() != null) {
                        // Convert BigDecimal to Long (use longValue)
                        price = item.getPrice().longValue();
                    }

                    return OrderItemDTO.builder()
                            .id(item.getId())
                            .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                            .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                            .quantity(item.getQuantity())
                            .price(price)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
