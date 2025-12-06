package org.mystore.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.Role;
import org.mystore.dtos.UserDTO;
import org.mystore.models.User;
import org.mystore.repositories.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UserProfileController {

    private final UserRepo userRepository;

    // ✅ Load Profile Page (Thymeleaf)
    @GetMapping
    public String profilePage() {
        return "profile";  // profile.html
    }

    // ✅ Secure API to Fetch Logged-in User Profile
    @GetMapping("/me")
    @ResponseBody
    public ResponseEntity<UserDTO> getMyProfile(Authentication authentication) {

        String email = authentication.getName(); // ✅ Extracted from JWT

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Convert User → UserDTO
        UserDTO dto = UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(
                        user.getRoleList()
                                .stream()
                                .map(role -> role.getRoleName())   // ✅ LAMBDA FIX
                                .collect(Collectors.toList())
                )
                .build();

        return ResponseEntity.ok(dto);
    }
}

