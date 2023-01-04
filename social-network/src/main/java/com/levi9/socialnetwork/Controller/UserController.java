package com.levi9.socialnetwork.Controller;

import com.levi9.socialnetwork.Exception.ResourceExistsException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Group;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Service.GroupService;
import com.levi9.socialnetwork.Service.MuteGroupService;
import com.levi9.socialnetwork.Service.UserService;
import com.levi9.socialnetwork.dto.RequestDTO;
import com.levi9.socialnetwork.dto.UserDTO;
import com.levi9.socialnetwork.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    MuteGroupService muteGroupService;

    @GetMapping
    public java.util.List<User> getAllUsers() {

        return this.userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable(value = "id") Long userId) throws ResourceNotFoundException {

        return new ResponseEntity<>(UserMapper.mapDTO(userService.getUserById(userId)), HttpStatus.OK);
    }

    @PostMapping("/{userId}/friend/{friendId}")
    public ResponseEntity<UserDTO> addFriend(@PathVariable(value = "userId") Long userId, @PathVariable(value = "friendId") Long friendId) throws ResourceNotFoundException {

        return new ResponseEntity<>(UserMapper.mapDTO(userService.addFriend(userId, friendId)), HttpStatus.OK);
    }

    @PutMapping("/{userId}/remove-friend/{friendId}")
    public ResponseEntity<Boolean> removeFriend(Principal principal, @PathVariable Long friendId)
            throws ResourceNotFoundException, ResourceExistsException {
        User user = userService.findUserByUsername(principal.getName());

        if(user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        boolean success = userService.removeFriend(user.getId(), friendId);
        return new ResponseEntity<>(success, HttpStatus.OK);

    }

    @PostMapping()
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User user1 = userService.createUser(user);
        return new ResponseEntity<>(user1, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable(value = "id") Long userId, @RequestBody User userDetails)
            throws ResourceNotFoundException {

        return new ResponseEntity<>(UserMapper.mapDTO(userService.updateUser(userId, userDetails)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long userId) throws ResourceNotFoundException {

        return userService.deleteUser(userId);
    }

    @PostMapping("/group-request")
    public ResponseEntity<User> createGroupRequest(@RequestBody RequestDTO requestDTO)
            throws ResourceNotFoundException, ResourceExistsException {

        User user;

        Group group = groupService.getGroupById(requestDTO.getIdGroup());
        if (group.isPrivate()) {
            user = userService.createGroupRequest(requestDTO);
        } else {
            user = groupService.addUserToGroup(requestDTO);
        }

        return ResponseEntity.ok().body(user);
    }
}
