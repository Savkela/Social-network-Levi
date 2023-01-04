package com.levi9.socialnetwork.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi9.socialnetwork.Controller.AuthController;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Model.UserVerificationStatus;
import com.levi9.socialnetwork.Security.authority.JWToken;
import com.levi9.socialnetwork.Service.RegistrationService;
import com.levi9.socialnetwork.Service.UserService;
import com.levi9.socialnetwork.dto.LoginRequestDTO;
import com.levi9.socialnetwork.dto.RegistrationRequestDTO;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    private static final String URL_PREFIX = "/api/auth";
    private static final String TOKEN = "token";
    
    @Autowired
    MockMvc mockMvc;
    
    @MockBean
    RegistrationService registrationService;
    
    @MockBean(name = "userService")
    UserService userService;
    
    @MockBean
    AuthenticationManager authenticationManager;
    
    @MockBean(name = "userDetailsService")
    UserDetailsService userDetailsService;
    
    @MockBean
    JWToken jwtToken;
    
    public void init() {
        
    }
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testRegistration() throws Exception {
        RegistrationRequestDTO registrationRequestDTO = RegistrationRequestDTO.builder()
                .name("John")
                .surname("Smith")
                .email("user1@mail.com")
                .username("user1")
                .password("123")
                .repeatedPassword("123").build();
        String requestBody = objectMapper.writeValueAsString(registrationRequestDTO);
        
        given(registrationService.register(registrationRequestDTO)).willReturn(TOKEN);
    
        mockMvc.perform(post(URL_PREFIX + "/registration").content(requestBody).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isOk());
    }
    
    @Test
    void testConfirmToken() throws Exception {
        given(registrationService.confirmToken(TOKEN)).willReturn(TOKEN);
        
        mockMvc.perform(get(URL_PREFIX + "/confirm").param("token", TOKEN))
                .andReturn();
        
        assertDoesNotThrow(() -> ResourceNotFoundException.class);
    }
    
    @Test
    void testLogin() throws Exception {
        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .username("user1")
                .password("123")
                .build();
        User user = User.builder()
                .id(1L)
                .name("John")
                .surname("Smith")
                .email("user1@mail.com")
                .username("user1")
                .password("123")
                .build();
               
        String requestBody = objectMapper.writeValueAsString(loginRequestDTO);
        given(userDetailsService.loadUserByUsername(loginRequestDTO.getUsername())).willReturn(user);
        given(userService.findUserByUsername(loginRequestDTO.getUsername())).willReturn(user);
        
        mockMvc.perform(post(URL_PREFIX + "/login").content(requestBody).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isOk());
    }
    
    @Test
    void testLoginShouldReturnForbiddenBecauseUserIsNotFound() throws Exception {
        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .username("user1")
                .password("123")
                .build();
        User user = User.builder()
                .id(1L)
                .name("John")
                .surname("Smith")
                .email("user1@mail.com")
                .username("user1")
                .password("123")
                .build();
               
        String requestBody = objectMapper.writeValueAsString(loginRequestDTO);
        given(userDetailsService.loadUserByUsername(loginRequestDTO.getUsername())).willReturn(user);
        given(userService.findUserByUsername(loginRequestDTO.getUsername())).willReturn(null);
        
        mockMvc.perform(post(URL_PREFIX + "/login").content(requestBody).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void testLoginShouldReturnForbiddenBecauseUserNotVerified() throws Exception {
        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .username("user1")
                .password("123")
                .build();
        User user = User.builder()
                .id(1L)
                .name("John")
                .surname("Smith")
                .email("user1@mail.com")
                .username("user1")
                .password("123")
                .build();
        user.setStatus(UserVerificationStatus.NOT_VERIFIED);
               
        String requestBody = objectMapper.writeValueAsString(loginRequestDTO);
        given(userDetailsService.loadUserByUsername(loginRequestDTO.getUsername())).willReturn(user);
        given(userService.findUserByUsername(loginRequestDTO.getUsername())).willReturn(user);
        
        mockMvc.perform(post(URL_PREFIX + "/login").content(requestBody).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void testLoginShouldReturnNotFound() throws Exception {
        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .username("user1")
                .password("123")              
                .build();
        User user = User.builder()
                .id(1L)
                .name("John")
                .surname("Smith")
                .email("user1@mail.com")
                .username("user1")
                .password("123")
                .build();
               
        String requestBody = objectMapper.writeValueAsString(loginRequestDTO);
        given(userDetailsService.loadUserByUsername(loginRequestDTO.getUsername())).willThrow(UsernameNotFoundException.class);
        given(userService.findUserByUsername(loginRequestDTO.getUsername())).willReturn(user);
        
        mockMvc.perform(post(URL_PREFIX + "/login").content(requestBody).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isNotFound());
    }
        
    
    @Test
    void testLoginShouldReturnForbiddenBecauseUserIsDisabled() throws Exception {
        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .username("user1")
                .password("123")              
                .build();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
        User user = User.builder()
                .id(1L)
                .name("John")
                .surname("Smith")
                .email("user1@mail.com")
                .username("user1")
                .password("123")
                .build();
               
        String requestBody = objectMapper.writeValueAsString(loginRequestDTO);
        given(userDetailsService.loadUserByUsername(loginRequestDTO.getUsername())).willReturn(user);
        given(userService.findUserByUsername(loginRequestDTO.getUsername())).willReturn(user);
        given(authenticationManager.authenticate(authenticationToken)).willThrow(DisabledException.class);
        
        mockMvc.perform(post(URL_PREFIX + "/login").content(requestBody).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void testLoginShouldReturnUnathorized() throws Exception {
        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .username("user1")
                .password("123")              
                .build();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
        User user = User.builder()
                .id(1L)
                .name("John")
                .surname("Smith")
                .email("user1@mail.com")
                .username("user1")
                .password("123")
                .build();
               
        String requestBody = objectMapper.writeValueAsString(loginRequestDTO);
        given(userDetailsService.loadUserByUsername(loginRequestDTO.getUsername())).willReturn(user);
        given(userService.findUserByUsername(loginRequestDTO.getUsername())).willReturn(user);
        given(authenticationManager.authenticate(authenticationToken)).willThrow(BadCredentialsException.class);
        
        mockMvc.perform(post(URL_PREFIX + "/login").content(requestBody).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isUnauthorized());
    }
    
}
