package org.mystore.controllers;

import lombok.AllArgsConstructor;
import org.mystore.dtos.OrderDTO;
import org.mystore.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seller/orders")
@AllArgsConstructor
@PreAuthorize("hasRole('SELLER')") // All endpoints here require SELLER role
public class OrderController {

    private final OrderService orderService;

    // ✅ Get all orders for the current seller
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getSellerOrders(@RequestParam Long sellerId) {
        System.out.println("sellerId from ordercontroller " + sellerId);
        List<OrderDTO> orders = orderService.getOrdersForSeller(sellerId);
        return ResponseEntity.ok(orders);
    }

    // ✅ Update order status (any status like PENDING, SHIPPED, DELIVERED, CANCELLED)
    @PutMapping("/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {

        try {
            orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok("Order status updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating order status: " + e.getMessage());
        }
    }
}
