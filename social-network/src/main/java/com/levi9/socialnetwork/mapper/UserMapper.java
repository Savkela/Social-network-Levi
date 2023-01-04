package com.levi9.socialnetwork.mapper;

import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.dto.UserDTO;

public class UserMapper {

    public static User mapEntity(UserDTO userDTO){
        return User.builder()
                .roles(userDTO.getRoles())
                .email(userDTO.getEmail())
                .friends(userDTO.getFriends())
                .id(userDTO.getId())
                .name(userDTO.getName())
                .status(userDTO.getStatus())
                .surname(userDTO.getSurname())
                .build();
    }

    public static UserDTO mapDTO(User user){
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .surname(user.getSurname())
                .friends(user.getFriends())
                .roles(user.getRoles())
                .status(user.getStatus())
                .build();
    }
}
