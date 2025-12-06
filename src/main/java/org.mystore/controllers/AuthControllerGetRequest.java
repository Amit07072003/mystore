package org.mystore.controllers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.mystore.dtos.SignupRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.crypto.SecretKey;

@Controller
public class AuthControllerGetRequest {

    @Autowired
    private SecretKey SECRET_KEY;

    @GetMapping("/user_profile")
    public String getUserProfile(HttpServletRequest request, Model model) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            model.addAttribute("error", "You are not logged in. Please login first.");
            return "login"; // redirect to login page if no token
        }

        try {
            String token = authHeader.substring(7);

            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.get("email", String.class);

            model.addAttribute("userEmail", email);
            return "user_profile"; // show profile page with email in dropdown

        } catch (Exception e) {
            model.addAttribute("error", "Invalid or expired token. Please login again.");
            return "login";
        }
    }

  //   Show Login Page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("signupRequest", new SignupRequestDTO());
        return "register";
    }

    @GetMapping("/home")
    public String showHomePage(Model model,
                               @CookieValue(name = "token", required = false) String token) {
        System.out.println("token: " + token);
        model.addAttribute("token", token);
        return "buyer/home"; // this is home.jsp or home.html
    }


}
