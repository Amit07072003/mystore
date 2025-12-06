package org.mystore.models;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseModel {
    private String name;

    private String email;

    private String password;

    private String phoneNumber;


    @ManyToMany
    private List<Role> roleList = new ArrayList<>();



//    public User(Long id) {
//        this.setId(id);
//    }

}


//1         M
//USER     ROLE
//M          1
//
//M    :  M