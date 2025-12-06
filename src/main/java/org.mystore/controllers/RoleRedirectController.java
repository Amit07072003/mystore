package org.mystore.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RoleRedirectController {

    @GetMapping("/dashboard")
    public String redirectBasedOnRole(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            switch (authority.getAuthority()) {
                case "ROLE_ADMIN": return "redirect:/admin/dashboard";
                case "ROLE_SELLER": return "redirect:/seller/dashboard";
                case "ROLE_USER": return "redirect:/buyer/dashboard";
            }
        }
        return "redirect:/login";
    }


}
