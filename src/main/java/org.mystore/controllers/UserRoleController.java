package org.mystore.controllers;

import org.mystore.dtos.UserDTO;
import org.mystore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/user/roles")
@PreAuthorize("hasRole('ADMIN')")  // Only admins can access
public class UserRoleController {


    private final UserService userService;

    public UserRoleController(UserService userService) {
        this.userService = userService;
    }

    // Get all users with roles
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Update roles for a specific user
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUserRoles(
            @PathVariable Long userId,
            @RequestBody Map<String, List<String>> body) {

        List<String> roles = body.get("roles");
        if (roles == null || roles.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        UserDTO updatedUser = userService.updateUserRoles(userId, roles);
        return ResponseEntity.ok(updatedUser);
    }
}

