package com.levi9.socialnetwork.dto;

import com.levi9.socialnetwork.Model.Role;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Model.UserVerificationStatus;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Data
@Builder
public class UserDTO {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private String username;
    private Set<Role> roles;
    private Set<User> friends;
    private UserVerificationStatus status;
}
