package org.mystore.services;

import lombok.AllArgsConstructor;
import org.mystore.dtos.UserDTO;
import org.mystore.models.Role;
import org.mystore.models.User;
import org.mystore.repositories.RoleRepo;
import org.mystore.repositories.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepository;
    private final RoleRepo roleRepository;

    // Fetch all users with their roles
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Update roles for a user
    public UserDTO updateUserRoles(Long userId, List<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        // Fetch existing roles from DB
        List<Role> roles = roleRepository.findAllByRoleNameIn(roleNames);
        user.setRoleList(roles); // Use roleList field

        return convertToDTO(userRepository.save(user));
    }

    // Convert User entity to DTO
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoleList().stream() // Use roleList field
                .map(Role::getRoleName)
                .collect(Collectors.toList()));
        return dto;
    }
}
