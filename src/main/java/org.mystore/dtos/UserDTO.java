package org.mystore.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private List<String> roles; // Added roles field

    public UserDTO(Long id) {
        this.id = id;
    }
}
