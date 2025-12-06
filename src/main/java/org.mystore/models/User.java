package org.mystore.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user") // avoid reserved keyword issues by explicitly naming
public class User extends BaseModel {

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String phoneNumber;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role_list", // explicit join table
        joinColumns = @JoinColumn(name = "user_id"), // FK to User
        inverseJoinColumns = @JoinColumn(name = "role_list_id") // FK to Role
    )
    private List<Role> roleList = new ArrayList<>();
}
