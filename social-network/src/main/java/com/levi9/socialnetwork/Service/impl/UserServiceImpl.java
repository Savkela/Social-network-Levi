package com.levi9.socialnetwork.Service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.levi9.socialnetwork.Exception.ResourceExistsException;
import com.levi9.socialnetwork.Controller.UserController;

import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Group;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Repository.GroupRepository;
import com.levi9.socialnetwork.Repository.UserRepository;
import com.levi9.socialnetwork.Service.EmailService;
import com.levi9.socialnetwork.Service.GroupService;
import com.levi9.socialnetwork.Service.UserService;
import com.levi9.socialnetwork.dto.RequestDTO;

@Service
public class UserServiceImpl implements UserService {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(UserController.class);

    public static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "User not found for this id : ";

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupService groupService;

    public java.util.List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public User getUserById(Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE + userId));
        return user;
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean removeFriend(Long userId, Long friendId) throws ResourceNotFoundException, ResourceExistsException {

        User user = userRepository.findById(userId).map(u -> u).orElseThrow();
        boolean removed = user.getFriends().removeIf(f -> f.getId().equals(friendId));

        if (!removed) {
            throw new ResourceNotFoundException("Friend with id " + friendId + " does not exist !");
        }

        userRepository.save(user);
        return removed;
    }

    public List<User> getNotMutedUsers(Long groupId) {
        return this.userRepository.getNotMutedUsers(groupId);
    }

    public User addFriend(Long userId, Long friendId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE + userId));

        User friend = userRepository.findById(friendId).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE + userId));

        user.getFriends().add(friend);
        userRepository.save(user);

        return user;

    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long userId, @RequestBody User userDetails)
            throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE + userId));

        user.setName(userDetails.getName());
        user.setSurname(userDetails.getSurname());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());

        return userRepository.save(user);
    }

    public Map<String, Boolean> deleteUser(Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE + userId));

        userRepository.delete(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    @Transactional
    public User createGroupRequest(RequestDTO requestDTO) throws ResourceNotFoundException, ResourceExistsException {

        Group group = groupService.getGroupById(requestDTO.getIdGroup());
        User user = userRepository.findById(requestDTO.getIdUser()).orElseThrow(
                () -> new ResourceNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE + requestDTO.getIdUser()));

        if (group.containsUserRequest(user.getId())) {
            throw new ResourceExistsException("Resource already exists.");
        }
        group.getUserRequests().add(user);
        groupRepository.save(group);

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        return user;
    }
}
