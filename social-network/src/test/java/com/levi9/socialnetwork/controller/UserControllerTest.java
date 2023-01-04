package com.levi9.socialnetwork.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi9.socialnetwork.Controller.GroupController;
import com.levi9.socialnetwork.Controller.UserController;
import com.levi9.socialnetwork.Exception.ResourceExistsException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Group;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Service.GroupService;
import com.levi9.socialnetwork.Service.MuteGroupService;
import com.levi9.socialnetwork.Service.UserService;
import com.levi9.socialnetwork.dto.RequestDTO;
import com.levi9.socialnetwork.dto.UserDTO;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;


@AutoConfigureMockMvc(addFilters = false)
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final String URL_PREFIX = "/api/users";
    private static final Long USER_ID = 1L;
    private static final String USERNAME = "user1";

    public static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "User not found for this id : ";


    @MockBean
    private UserService userService;

    @MockBean
    private GroupService groupService;

    @MockBean
    MuteGroupService muteGroupService;

    @Autowired
    MockMvc mockMvc;

    Principal principal;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        principal = new Principal() {

            @Override
            public String getName() {
                return USERNAME;
            }
        };

    }

    @Test
    void testGetAllUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        when(userService.getAllUsers()).thenReturn(users);
        mockMvc.perform(get(URL_PREFIX)).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(users.size())));
    }

    @Test
    void testGetUserById() throws Exception {

        User user = User.builder()
                .id(USER_ID)
                .username(USERNAME)
                .email("user@test.com")
                .build();

        when(userService.getUserById(USER_ID)).thenReturn(user);
        mockMvc.perform(get(URL_PREFIX + "/" + USER_ID)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(USERNAME)))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andReturn();
    }

    @Test
    void testGetUserByIdShouldThrowException() throws Exception {

        User user = User.builder()
                .id(USER_ID)
                .username(USERNAME)
                .email("user@test.com")
                .build();

        when(userService.getUserById(USER_ID)).thenAnswer(invocation -> {
            return new ResourceNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE + USER_ID);
        });
        mockMvc.perform(get(URL_PREFIX  + USER_ID)).andExpect(status().isNotFound());
    }

    @Test
    void testAddFriend() throws Exception {
        User user = User.builder()
                .id(USER_ID)
                .username(USERNAME)
                .email("user@test.com")
                .friends(Set.of(User.builder().id(2L).build(), User.builder().id(3L).build()))
                .build();
        when(userService.addFriend(USER_ID, 2L)).thenReturn(user);
        mockMvc.perform(post(URL_PREFIX + "/" + USER_ID + "/friend/" + 2L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(USER_ID.intValue())))
                .andExpect(jsonPath("$.username", is(USERNAME)))
                .andExpect(jsonPath("$.friends.size()", is(2)))
                .andReturn();


    }

    @Test
    void testRemoveFriend() throws Exception {
        User user = User.builder()
                .id(USER_ID)
                .username(USERNAME)
                .email("user@test.com")
                .friends(Set.of(User.builder().id(2L).build(), User.builder().id(3L).build()))
                .build();
        when(userService.findUserByUsername(principal.getName())).thenReturn(user);
        when(userService.removeFriend(USER_ID, 2L)).thenReturn(true);
        mockMvc.perform(put(URL_PREFIX + "/" + "1" + "/remove-friend/" + 2L).principal(principal)).andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)))
                .andReturn();
    }

    @Test
    void testRemoveFriendUnauthorized() throws Exception {
        when(userService.findUserByUsername(principal.getName())).thenReturn(null);
        mockMvc.perform(put(URL_PREFIX + "/" + "1" + "/remove-friend/" + 2L).principal(principal)).andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateUser() throws Exception {
        User user = User.builder()
                .id(USER_ID)
                .username(USERNAME)
                .email("user@test.com")
                .friends(Set.of(User.builder().id(2L).build(), User.builder().id(3L).build()))
                .build();
        when(userService.createUser(user)).thenReturn(user);
        mockMvc.perform(post(URL_PREFIX).content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.email", is("user@test.com")))
                .andExpect(jsonPath("$.friends.size()", is(2)))
                .andReturn();
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = User.builder()
                .id(USER_ID)
                .username(USERNAME)
                .email("user@test.com")
                .friends(Set.of(User.builder().id(2L).build(), User.builder().id(3L).build()))
                .build();
        when(userService.updateUser(USER_ID, user)).thenReturn(user);
        mockMvc.perform(put(URL_PREFIX + "/" + USER_ID).content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.email", is("user@test.com")))
                .andExpect(jsonPath("$.friends.size()", is(2)))
                .andReturn();

    }

    @Test
    void testDeleteUser() throws Exception {
        Map map = new HashMap();
        map.put("deleted", true);
        when(userService.deleteUser(USER_ID)).thenReturn(map);
        mockMvc.perform(delete(URL_PREFIX + "/" + USER_ID)).andExpect(status().isOk());
    }

    @Test
    void testCreateGroupRequestForPrivateGroup() throws Exception {
        Group group = Group.builder()
                .id(5L)
                .isPrivate(true)
                .userRequests(new ArrayList<>())
                .build();
        User user = User.builder()
                .id(USER_ID)
                .username(USERNAME)
                .email("user@test.com")
                .friends(Set.of(User.builder().id(2L).build(), User.builder().id(3L).build()))
                .build();
        RequestDTO requestDTO  = RequestDTO.builder().idGroup(5L).idUser(USER_ID).build();

        when(groupService.getGroupById(5L)).thenReturn(group);
        when(userService.createGroupRequest(requestDTO)).thenReturn(user);
        when(groupService.addUserToGroup(requestDTO)).thenReturn(user);
        mockMvc.perform(post(URL_PREFIX + "/group-request").content(objectMapper.writeValueAsString(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateGroupRequestForPublicGroup() throws Exception {
        Group group = Group.builder()
                .id(5L)
                .isPrivate(false)
                .userRequests(new ArrayList<>())
                .build();
        User user = User.builder()
                .id(USER_ID)
                .username(USERNAME)
                .email("user@test.com")
                .friends(Set.of(User.builder().id(2L).build(), User.builder().id(3L).build()))
                .build();
        RequestDTO requestDTO  = RequestDTO.builder().idGroup(5L).idUser(USER_ID).build();

        when(groupService.getGroupById(5L)).thenReturn(group);
        when(userService.createGroupRequest(requestDTO)).thenReturn(user);
        when(groupService.addUserToGroup(requestDTO)).thenReturn(user);
        mockMvc.perform(post(URL_PREFIX + "/group-request").content(objectMapper.writeValueAsString(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());
    }
}