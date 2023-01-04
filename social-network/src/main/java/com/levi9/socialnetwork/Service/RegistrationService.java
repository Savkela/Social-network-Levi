package com.levi9.socialnetwork.Service;

import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.dto.RegistrationRequestDTO;

import java.io.IOException;

public interface RegistrationService {

    String register(RegistrationRequestDTO registrationRequestDTO) throws IOException, ResourceNotFoundException;

    String confirmToken(String token) throws ResourceNotFoundException;
}
