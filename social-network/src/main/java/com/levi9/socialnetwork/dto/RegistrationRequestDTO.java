package com.levi9.socialnetwork.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationRequestDTO {

    private String name;
    private String surname;
    private String email;
    private String username;
    private String password;
    private String repeatedPassword;
}
