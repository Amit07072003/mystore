package org.mystore.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartPageController {

//    @PreAuthorize("hasRole('USER')")
    @GetMapping("/cart")
    public String cartPage() {
        return "cart"; // Thymeleaf or static HTML page
    }
}
