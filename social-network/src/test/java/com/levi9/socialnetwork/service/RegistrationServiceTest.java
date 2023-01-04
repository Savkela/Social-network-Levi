package com.levi9.socialnetwork.service;

import com.levi9.socialnetwork.Exception.ResourceConflictException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.ConfirmationToken;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Model.UserVerificationStatus;
import com.levi9.socialnetwork.Security.authority.JWToken;
import com.levi9.socialnetwork.Service.ConfirmationTokenService;
import com.levi9.socialnetwork.Service.EmailService;
import com.levi9.socialnetwork.Service.UserService;
import com.levi9.socialnetwork.Service.impl.RegistrationServiceImpl;
import com.levi9.socialnetwork.dto.RegistrationRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
class RegistrationServiceTest {

    @Mock
    private JWToken jwToken;

    @Mock
    private EmailService emailService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ConfirmationTokenService confirmationTokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    private static final String encodedPassword = "dnasdsandsadoasdndsandsadiosadnsada";
    private static final String token = "wqenqwnxuqonxiwepwke12bbjnkl32";

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    void testValidateRegistrationRequestIsValid(){
        RegistrationRequestDTO registrationRequestDTO =
                RegistrationRequestDTO.builder()
                        .name("John")
                        .surname("Doe")
                        .email("john@test.com")
                        .username("john123")
                        .password("123456")
                        .repeatedPassword("123456")
                        .build();

        boolean isValid = RegistrationServiceImpl.validateRegistrationRequest(registrationRequestDTO);
        assertEquals(true, isValid);

    }

    @Test
    void testValidateRegistrationRequestNameIsNotValid(){
        RegistrationRequestDTO registrationRequestDTO =
                RegistrationRequestDTO.builder()
                        .name("")
                        .surname("Doe")
                        .email("john@test.com")
                        .username("john123")
                        .password("123456")
                        .repeatedPassword("123456")
                        .build();

        boolean isValid = RegistrationServiceImpl.validateRegistrationRequest(registrationRequestDTO);
        assertEquals(false, isValid);
    }

    @Test
    void testValidateRegistrationRequestSurnameIsNotValid(){
        RegistrationRequestDTO registrationRequestDTO =
                RegistrationRequestDTO.builder()
                        .name("John")
                        .surname("")
                        .email("john@test.com")
                        .username("john123")
                        .password("123456")
                        .repeatedPassword("123456")
                        .build();

        boolean isValid = RegistrationServiceImpl.validateRegistrationRequest(registrationRequestDTO);
        assertEquals(false, isValid);
    }

    @Test
    void testValidateRegistrationRequestPasswordIsNotValid(){
        RegistrationRequestDTO registrationRequestDTO =
                RegistrationRequestDTO.builder()
                        .name("John")
                        .surname("Doe")
                        .email("john@test.com")
                        .username("john123")
                        .password("12345")
                        .repeatedPassword("123456")
                        .build();

        boolean isValid = RegistrationServiceImpl.validateRegistrationRequest(registrationRequestDTO);
        assertEquals(false, isValid);
    }

    @Test
    void testValidateRegistrationRequestRepeatedPasswordIsNotValid(){
        RegistrationRequestDTO registrationRequestDTO =
                RegistrationRequestDTO.builder()
                        .name("John")
                        .surname("Doe")
                        .email("john@test.com")
                        .username("john123")
                        .password("123456")
                        .repeatedPassword("12345")
                        .build();

        boolean isValid = RegistrationServiceImpl.validateRegistrationRequest(registrationRequestDTO);
        assertEquals(false, isValid);
    }

    @Test
    void testValidateRegistrationRequestPasswordsDoNotMatch(){
        RegistrationRequestDTO registrationRequestDTO =
                RegistrationRequestDTO.builder()
                        .name("John")
                        .surname("Doe")
                        .email("john@test.com")
                        .username("john123")
                        .password("12345678")
                        .repeatedPassword("12345789")
                        .build();

        boolean isValid = RegistrationServiceImpl.validateRegistrationRequest(registrationRequestDTO);
        assertEquals(false, isValid);
    }

    @Test
    void testValidateRegistrationRequestEmailIsNotValid(){
        RegistrationRequestDTO registrationRequestDTO =
                RegistrationRequestDTO.builder()
                        .name("John")
                        .surname("Doe")
                        .email("john@test")
                        .username("john123")
                        .password("123456")
                        .repeatedPassword("123456")
                        .build();

        boolean isValid = RegistrationServiceImpl.validateRegistrationRequest(registrationRequestDTO);
        assertEquals(false, isValid);
    }


    @Test
    void testRegister() throws ResourceNotFoundException, IOException {
        RegistrationRequestDTO registrationRequestDTO =
                RegistrationRequestDTO.builder()
                        .name("John")
                        .surname("Doe")
                        .email("john@test.com")
                        .username("john123")
                        .password("123456")
                        .repeatedPassword("123456")
                        .build();

        when(userService.findUserByUsername(registrationRequestDTO.getUsername())).thenReturn(null);
        when(passwordEncoder.encode(registrationRequestDTO.getPassword())).thenReturn(encodedPassword);

        User user = User.builder()
                .id(1L)
                .name(registrationRequestDTO.getName())
                .surname(registrationRequestDTO.getSurname())
                .email(registrationRequestDTO.getEmail())
                .username(registrationRequestDTO.getUsername())
                .password(encodedPassword)
                .status(UserVerificationStatus.NOT_VERIFIED)
                .build();

        when(userService.createUser(any(User.class))).thenReturn(user);
        when(userDetailsService.loadUserByUsername("john123")).thenReturn(user);
        when(jwToken.generateRegistrationToken(user)).thenReturn(token);

        ConfirmationToken confirmationToken = new ConfirmationToken(token, null, 1L);

        when(confirmationTokenService.save(confirmationToken)).thenReturn(confirmationToken);

        String acutalToken = registrationService.register(registrationRequestDTO);

        assertEquals(RegistrationServiceTest.token, acutalToken);

    }

    @Test
    void testRegisterUsernameAlreadyExist() throws ResourceNotFoundException {
        RegistrationRequestDTO registrationRequestDTO =
                RegistrationRequestDTO.builder()
                        .name("John")
                        .surname("Doe")
                        .email("john@test.com")
                        .username("john123")
                        .password("123456")
                        .repeatedPassword("123456")
                        .build();

        User user = User.builder()
                .id(1L)
                .name(registrationRequestDTO.getName())
                .surname(registrationRequestDTO.getSurname())
                .email(registrationRequestDTO.getEmail())
                .username(registrationRequestDTO.getUsername())
                .password(encodedPassword)
                .status(UserVerificationStatus.NOT_VERIFIED)
                .build();

        when(userService.findUserByUsername(registrationRequestDTO.getUsername())).thenReturn(user);

        assertThrows(ResourceConflictException.class, () -> {
            registrationService.register(registrationRequestDTO);
        });
    }

    @Test
    void testRegisterRequestNotValid() throws ResourceNotFoundException {
        RegistrationRequestDTO registrationRequestDTO =
                RegistrationRequestDTO.builder()
                        .name("John")
                        .surname("Doe")
                        .email("john@test.com")
                        .username("john123")
                        .password("12345")
                        .repeatedPassword("123456")
                        .build();

        assertThrows(IllegalStateException.class, () -> {
            registrationService.register(registrationRequestDTO);
        });
    }

    @Test
    void testConfirmToken() throws ResourceNotFoundException {
        ConfirmationToken confirmationToken = new ConfirmationToken(token, null, 1L);

        User user = User.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john@test.com")
                .username("john123")
                .password(encodedPassword)
                .status(UserVerificationStatus.NOT_VERIFIED)
                .build();

        when(confirmationTokenService.findByToken(token)).thenReturn(confirmationToken);
        when(userService.getUserById(confirmationToken.getUserId())).thenReturn(user);
        when(confirmationTokenService.save(confirmationToken)).thenReturn(confirmationToken);
        when(userService.createUser(user)).thenReturn(user);

        String returnedMessage = registrationService.confirmToken(token);

        assertEquals("Email " + user.getEmail() + " successfully confirmed!", returnedMessage);
    }

    @Test
    void testConfirmTokenAlreadyConfrmed() throws ResourceNotFoundException {
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), 1L);

        User user = User.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john@test.com")
                .username("john123")
                .password(encodedPassword)
                .status(UserVerificationStatus.NOT_VERIFIED)
                .build();

        when(confirmationTokenService.findByToken(token)).thenReturn(confirmationToken);
        when(userService.getUserById(confirmationToken.getUserId())).thenReturn(user);

        assertThrows(IllegalStateException.class, () -> {
            registrationService.confirmToken(token);
        });

    }
}