package org.mystore.controllers;

import org.mystore.services.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEmailController {

    private final EmailService emailService;

    public TestEmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/test-email")
    public String sendTestEmail() {
        String to = "kushwahaamit0707@gmail.com"; // send to yourself
        String link = "http://localhost:8080/resetpassword?token=TEST123";
        try {
            emailService.sendPasswordResetEmail(to, link);
            return "✅ Test email sent successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Failed to send email: " + e.getMessage();
        }
    }
}
