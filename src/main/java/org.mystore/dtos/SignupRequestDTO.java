package org.mystore.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDTO {

    private String name;
    private String email;
    private String password;
    private String phoneNumber;
}
