package com.levi9.socialnetwork.service;

import com.levi9.socialnetwork.Exception.ResourceExistsException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Group;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Repository.GroupRepository;
import com.levi9.socialnetwork.Repository.UserRepository;
import com.levi9.socialnetwork.Service.GroupService;
import com.levi9.socialnetwork.Service.impl.UserServiceImpl;
import com.levi9.socialnetwork.dto.RequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GroupService groupService;

    @InjectMocks
    private UserServiceImpl userService;

    private static final Long userId  = 1L;
    private static final String username = "User1";
    public static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "User not found for this id : ";


    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    void testGetAllUsers() {
        given(userRepository.findAll()).willReturn(List.of(new User(), new User(), new User()));

        assertThat(userService.getAllUsers()).hasSize(3);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserByIdShouldReturnOneUser() throws ResourceNotFoundException {
        User expectedUser = User.builder()
                .id(userId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(expectedUser));

        User actualUser = userService.getUserById(userId);

        assertThat(expectedUser).usingRecursiveComparison().isEqualTo(actualUser);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testGetUserByIdShouldThrowResourceNotFoundException(){
        User expectedUser = User.builder()
                .id(userId)
                .build();

        given(userRepository.findById(userId)).willAnswer(invocation -> {
            throw new ResourceNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE + userId);
        });

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(userId);
        });

    }

    @Test
    void testFindUserByUsernameShouldReturnOneUser() {
        User expectedUser = User.builder()
                .id(userId)
                .build();
        when(userRepository.findByUsername(username)).thenReturn(expectedUser);

        User actualUser = userService.findUserByUsername(username);

        assertThat(expectedUser).usingRecursiveComparison().isEqualTo(actualUser);
        verify(userRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testCreateUser() {
        User expectedUser = User.builder()
                .id(userId)
                .name("user")
                .surname("surname")
                .email("user@test.com")
                .username("user123")
                .build();
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        User actualUser = userService.createUser(expectedUser);

        assertThat(expectedUser).usingRecursiveComparison().isEqualTo(actualUser);
        verify(userRepository, times(1)).save(expectedUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testAddFriend() throws ResourceNotFoundException {
        User user = User.builder()
                .id(userId)
                .name("user")
                .surname("surname")
                .email("user@test.com")
                .username("user123")
                .friends(new HashSet<>())
                .build();

        User friend = User.builder()
                .id(2L)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(friend));

        User returnedUser = userService.addFriend(userId, 2L);

        assertEquals(1, returnedUser.getFriends().size());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(2L);
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testRemoveFriend() throws ResourceExistsException, ResourceNotFoundException {
        User friend1 = User.builder()
                .id(2L)
                .build();

        User friend2 = User.builder()
                .id(3L)
                .build();


        User user = User.builder()
                .id(userId)
                .name("user")
                .surname("surname")
                .email("user@test.com")
                .username("user123")
                .friends(new HashSet<>())
                .build();

        user.getFriends().add(friend1);
        user.getFriends().add(friend2);

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));

        boolean removed = userService.removeFriend(userId, 2L);

        assertEquals(true, removed);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testUpdateUser() throws ResourceNotFoundException {
        User expectedUser = User.builder()
                .id(userId)
                .name("user")
                .surname("surname")
                .email("user@test.com")
                .username("user123")
                .friends(new HashSet<>())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        User actualUser = userService.updateUser(userId, expectedUser);

        assertThat(actualUser).usingRecursiveComparison().isEqualTo(expectedUser);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(expectedUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testUpdateUserShouldThrowResourceNotFoundException() throws ResourceNotFoundException {
        User expectedUser = User.builder()
                .id(userId)
                .name("user")
                .surname("surname")
                .email("user@test.com")
                .username("user123")
                .friends(new HashSet<>())
                .build();

        given(userRepository.findById(userId)).willAnswer(invocation -> {
            throw new ResourceNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE + userId);
        });

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(userId, expectedUser);
        });
    }

    @Test
    void createGroupRequest() throws ResourceNotFoundException, ResourceExistsException {

        Group group = Group.builder()
                .id(3L)
                .idAdmin(userId)
                .isPrivate(false)
                .userRequests(new ArrayList<>())
                .build();

        User user = User.builder()
                .id(userId)
                .name("user")
                .surname("surname")
                .email("user@test.com")
                .username("user123")
                .friends(new HashSet<>())
                .build();

        RequestDTO requestDTO = new RequestDTO(userId, 3L);

        when(groupService.getGroupById(3L)).thenReturn(group);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));

        userService.createGroupRequest(requestDTO);

        verify(groupService, times(1)).getGroupById(3L);
        verify(groupRepository, times(1)).save(group);
    }

    @Test
    void deleteUser() throws ResourceNotFoundException {
        User user = User.builder()
                .id(userId)
                .name("user")
                .surname("surname")
                .email("user@test.com")
                .username("user123")
                .friends(new HashSet<>())
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));

        userService.deleteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(user);
    }
}