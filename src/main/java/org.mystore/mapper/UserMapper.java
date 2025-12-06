package org.mystore.mapper;

import org.mystore.dtos.UserDTO;
import org.mystore.models.Role;
import org.mystore.models.User;

import java.util.stream.Collectors;


public class UserMapper {
    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoleList().stream() // Use roleList field
                        .map(Role::getRoleName)
                        .collect(Collectors.toList()))
                .build();


    }
    public static User toUserEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;

        }
        return User.builder()

                .build();
    }

    public static User toUserEntityWithId(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        return user;
    }

}
