package com.levi9.socialnetwork.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;

import com.levi9.socialnetwork.Exception.ResourceExistsException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Comment;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.dto.RequestDTO;

public interface UserService extends UserDetailsService {

    public java.util.List<User> getAllUsers();

    public User getUserById(Long userId) throws ResourceNotFoundException;
    
    public User findUserByUsername(String username) throws ResourceNotFoundException;

    public User createUser(User user);

    public User addFriend(Long userId, Long friendId) throws ResourceNotFoundException;

    public boolean removeFriend(Long userId, Long friendId) throws ResourceNotFoundException, ResourceExistsException;

    public User updateUser(Long userId, @RequestBody User userDetails) throws ResourceNotFoundException;

    public User createGroupRequest(RequestDTO requestDTO) throws ResourceNotFoundException, ResourceExistsException;

    public Map<String, Boolean> deleteUser(Long userId) throws ResourceNotFoundException;

}
