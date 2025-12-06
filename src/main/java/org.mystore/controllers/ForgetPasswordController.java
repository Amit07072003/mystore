package org.mystore.controllers;

import org.mystore.models.PasswordResetToken;
import org.mystore.models.User;
import org.mystore.repositories.PasswordResetTokenRepository;
import org.mystore.repositories.UserRepo;
import org.mystore.services.EmailService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ForgetPasswordController {


    private PasswordResetTokenRepository tokenRepo;

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;   // assume you have a service for sending emails
    private final String appBaseUrl = "http://localhost:8080"; // configure properly

    public ForgetPasswordController(UserRepo userRepo,
                                    PasswordEncoder passwordEncoder,
                                    EmailService emailService,
                                    PasswordResetTokenRepository tokenRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tokenRepo = tokenRepo;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forget-password"; // keep consistent
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "No user found with that email.");
            return "forget-password";
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);

        Optional<PasswordResetToken> existingTokenOpt = tokenRepo.findByUser(Optional.of(user));
        PasswordResetToken resetToken = existingTokenOpt.orElse(new PasswordResetToken());
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(expiry);
        tokenRepo.save(resetToken);

        String resetLink = appBaseUrl + "/resetpassword?token=" + token;
        System.out.println("Password reset link: " + resetLink);
        System.out.println("email: " + user.getEmail());
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);

        model.addAttribute("message", "Password reset link sent to your email.");
        return "forget-password";
    }

    @GetMapping("/resetpassword")
    public String showResetForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/resetpassword")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String newPassword,
                                       Model model) {

        Optional<PasswordResetToken> resetTokenOpt = tokenRepo.findByToken(token);
        if (resetTokenOpt.isEmpty()) {
            model.addAttribute("error", "Invalid token");
            return "reset_password";
        }

        PasswordResetToken resetToken = resetTokenOpt.get();
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Token expired");
            return "reset_password";
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        tokenRepo.delete(resetToken);

        model.addAttribute("message", "Password reset successful!");
        return "login";
    }
}
