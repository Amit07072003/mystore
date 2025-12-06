package org.mystore.controllers;

import lombok.RequiredArgsConstructor;
import org.mystore.dtos.OrderDTO;
import org.mystore.models.Order;
import org.mystore.models.User;
import org.mystore.repositories.UserRepo;

import org.mystore.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/seller")
@PreAuthorize("hasRole('SELLER')")
@RequiredArgsConstructor
public class SellerController {
    private final OrderService orderService;

    private final UserRepo userRepository;

    // ✅ Seller dashboard
    @GetMapping("/dashboard")
    public String sellerDashboard(Model model) {
        // Get logged-in user's email or username
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // Fetch user from DB
        User user = userRepository.findByEmail(email).orElse(null);


        // Add to model for Thymeleaf access
        model.addAttribute("user", user);

        // Return dashboard page
        return "seller/seller_dashboard";
    }
    @GetMapping("/orders/{sellerId}")
    public ResponseEntity<List<OrderDTO>> getSellerOrders(@PathVariable Long sellerId) {
        List<OrderDTO> orders = orderService.getOrdersForSeller(sellerId);
        return ResponseEntity.ok(orders);
    }
}
