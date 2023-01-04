package com.levi9.socialnetwork.Controller;

import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Model.UserVerificationStatus;
import com.levi9.socialnetwork.Security.authority.JWToken;
import com.levi9.socialnetwork.Service.RegistrationService;
import com.levi9.socialnetwork.Service.UserService;
import com.levi9.socialnetwork.dto.LoginRequestDTO;
import com.levi9.socialnetwork.dto.RegistrationRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private JWToken jwToken;

    @PostMapping(value = "/registration")
    public ResponseEntity<Void> registration(@RequestBody RegistrationRequestDTO registrationRequestDTO)
            throws IOException, ResourceNotFoundException {

        registrationService.register(registrationRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/confirm")
    public String confirmToken(@RequestParam String token) throws ResourceNotFoundException {

        return registrationService.confirmToken(token);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            User user = userService.findUserByUsername(loginRequest.getUsername());

            if (user == null || user.getStatus() == UserVerificationStatus.NOT_VERIFIED) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            return new ResponseEntity<>(jwToken.generateToken(userDetails.getUsername()), HttpStatus.OK);

        } catch (UsernameNotFoundException | ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (DisabledException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }
}
