package com.levi9.socialnetwork.Service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.levi9.socialnetwork.Exception.ResourceConflictException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.ConfirmationToken;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Model.UserVerificationStatus;
import com.levi9.socialnetwork.Security.authority.JWToken;
import com.levi9.socialnetwork.Service.ConfirmationTokenService;
import com.levi9.socialnetwork.Service.EmailService;
import com.levi9.socialnetwork.Service.RegistrationService;
import com.levi9.socialnetwork.Service.UserService;
import com.levi9.socialnetwork.dto.RegistrationRequestDTO;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private JWToken jwToken;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ConfirmationTokenService confirmationTokenService;

    @Autowired
    private UserService userService;

    public static int PASSWORD_MIN_LENGTH = 6;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean validateRegistrationRequest(RegistrationRequestDTO registrationRequestDTO) {
        if (registrationRequestDTO.getName().isBlank() || registrationRequestDTO.getSurname().isBlank()
                || registrationRequestDTO.getPassword().length() < PASSWORD_MIN_LENGTH
                || registrationRequestDTO.getRepeatedPassword().length() < PASSWORD_MIN_LENGTH
                || !registrationRequestDTO.getPassword().equals(registrationRequestDTO.getRepeatedPassword())) {
            return false;
        }

        return validate(registrationRequestDTO.getEmail());
    }

    @Transactional
    @Override
    public String register(RegistrationRequestDTO registrationRequestDTO) throws IOException, ResourceNotFoundException {
        boolean isRequestValid = validateRegistrationRequest(registrationRequestDTO);
        if (!isRequestValid) {
            throw new IllegalStateException("Registration request is not valid!");
        }

        User user = this.userService.findUserByUsername(registrationRequestDTO.getUsername());
        if (user != null) {
            throw new ResourceConflictException(user.getId(), "Username already exists");
        }

        String encodedPassword = passwordEncoder.encode(registrationRequestDTO.getPassword());

        User registeredUser = new User(registrationRequestDTO.getName(), registrationRequestDTO.getSurname(),
                registrationRequestDTO.getEmail(), registrationRequestDTO.getUsername(), encodedPassword,
                UserVerificationStatus.NOT_VERIFIED);

        registeredUser = userService.createUser(registeredUser);

        UserDetails userDetails = userDetailsService.loadUserByUsername(registeredUser.getUsername());

        String token = jwToken.generateRegistrationToken(userDetails);

        ConfirmationToken confirmationToken = new ConfirmationToken(token, null, registeredUser.getId());
        confirmationTokenService.save(confirmationToken);

        String link = "http://localhost:8762/social-network/api/auth/confirm?token=" + confirmationToken.getToken();
        try {
            emailService.sendEmail(registeredUser.getEmail(),
                    emailService.registerEmail(registeredUser.getName(), link), "Verify your email");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return token;
    }

    @Transactional
    @Override
    public String confirmToken(String token) throws ResourceNotFoundException {
        ConfirmationToken confirmationToken = confirmationTokenService.findByToken(token);
        User user = userService.getUserById(confirmationToken.getUserId());

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());

        confirmationTokenService.save(confirmationToken);

        user.setStatus(UserVerificationStatus.VERIFIED);
        userService.createUser(user);

        return "Email " + user.getEmail() + " successfully confirmed!";
    }
}
