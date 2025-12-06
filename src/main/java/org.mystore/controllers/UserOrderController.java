package org.mystore.controllers;

import lombok.RequiredArgsConstructor;
import org.mystore.dtos.OrderDTO;
import org.mystore.models.User;
import org.mystore.repositories.UserRepo;
import org.mystore.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UserOrderController {

    private final OrderService orderService;
    private final UserRepo userRepository;

    // ✅ Page Navigation
    @GetMapping
    public String myOrdersPage() {
        return "my_orders";
    }

    // ✅ API (JWT Secured)
    @GetMapping("/my")
    @ResponseBody
    public ResponseEntity<List<OrderDTO>> getMyOrders(Authentication authentication) {

        String email = authentication.getName();   // ✅ Extracted from JWT

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<OrderDTO> orders = orderService.getOrdersForUser(user.getId());

        // ===== Print all data in console =====
        if (orders.isEmpty()) {
            System.out.println("No orders found for user: " + email);
        } else {
            for (OrderDTO order : orders) {
                System.out.println("Order ID: " + order.getId());
                System.out.println("Razorpay Order ID: " + order.getRazorpayOrderId());
                System.out.println("Order Date: " + order.getOrderDate());
                System.out.println("Status: " + order.getStatus());
                System.out.println("Total Amount: " + order.getTotalAmount());
                System.out.println("User ID: " + order.getUserId());
                System.out.println("User Email: " + order.getUserEmail());

                if (order.getItems() != null && !order.getItems().isEmpty()) {
                    for (var item : order.getItems()) {
                        System.out.println("   Product Name: " + item.getProductName());
                        System.out.println("   Quantity: " + item.getQuantity());
                        System.out.println("   Price: " + item.getPrice());
                    }
                } else {
                    System.out.println("   No items in this order");
                }
                System.out.println("--------------------------------------------------");
            }
        }

        return ResponseEntity.ok(orders);
    }

}

