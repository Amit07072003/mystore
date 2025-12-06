package org.mystore.controllers;

import lombok.RequiredArgsConstructor;
import org.mystore.dtos.CartDTO;
import org.mystore.dtos.InitiatePaymentRequestDto;
import org.mystore.dtos.UserDTO;
import org.mystore.models.Order;
import org.mystore.services.AuthServiceImpl;
import org.mystore.services.CartService;
import org.mystore.services.OrderService;
import org.mystore.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final CartService cartService;
    private final AuthServiceImpl authService;
    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkoutCart(
            @CookieValue(value = "token", required = false) String token,
            @RequestBody InitiatePaymentRequestDto dto) {

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid token"));
        }

        // Authenticate user
        UserDTO userDTO = authService.getUserFromToken(token);
        if (userDTO == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        // Fetch user's cart
        CartDTO cart = cartService.getCart(userDTO);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
        }

        // Determine total amount
        Long totalAmount = (dto.getAmount() != null && dto.getAmount() > 0)
                ? dto.getAmount()
                : cart.getTotalAmount();

        // Validate phone
        String phone = dto.getPhone().replaceAll("[^0-9]", "");
        if (phone.length() != 10) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid phone number. Must be 10 digits."));
        }

        try {
            String paymentResponse = paymentService.initiatePayment(
                    totalAmount,
                    phone,
                    dto.getEmail(),
                    userDTO.getName()
            );

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "amount", totalAmount,
                    "paymentResponse", paymentResponse
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Payment initiation failed: " + e.getMessage()));
        }
    }

    @PostMapping("/success")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> payload) {
        try {
            // razorpayOrderId is String in Order entity
            String razorpayOrderId = payload.get("razorpay_order_id").toString();

            // Fetch order using razorpayOrderId (String)
            Order order = orderService.getOrderByRazorpayOrderId(razorpayOrderId);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid order reference ID"));
            }

            Long amount = order.getTotalAmount();
            Long userId = order.getUser().getId();

            // You can call processSuccessfulPayment if you want to update status, stock, etc.
//            orderService.processSuccessfulPayment(razorpayOrderId, amount, userId);

            return ResponseEntity.ok(Map.of(
                    "message", "Payment successful!",
                    "orderId", razorpayOrderId,
                    "amount", amount
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to confirm payment"));
        }
    }
}
