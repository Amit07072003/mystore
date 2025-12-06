package org.mystore.controllers;

import org.mystore.dtos.UserDTO;
import org.mystore.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/buyer")
@PreAuthorize("hasRole('USER')")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // User dashboard
    @GetMapping("/dashboard")
    public String userDashboard() {
        return "buyer/home";
    }

//    @GetMapping("/orders")
//    public String userOrders(Model model, Principal principal) {
//        Long userId = userService.getUserIdFromEmail(principal.getName());
//        model.addAttribute("userId", userId);
//        return "my_orders";
//
//    }





//    // Cart operations
//    @PostMapping("/cart/add/{productId}")
//    public String addToCart(@PathVariable Long productId) {
//        return "Added product to cart: " + productId;
//    }
//
//    @PostMapping("/cart/remove/{productId}")
//    public String removeFromCart(@PathVariable Long productId) {
//        return "Removed product from cart: " + productId;
//    }

    // Add more user-specific endpoints here
}
