package com.levi9.socialnetwork.Service.impl;

import com.levi9.socialnetwork.Model.ConfirmationToken;
import com.levi9.socialnetwork.Repository.ConfirmationTokenRepository;
import com.levi9.socialnetwork.Service.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Override
    public ConfirmationToken findByToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    @Override
    public ConfirmationToken findByUserId(Long id) {
        return confirmationTokenRepository.findByUserId(id);
    }

    @Override
    public int updateConfirmedAt(String token, LocalDateTime confirmedAt) {
        return confirmationTokenRepository.updateConfirmedAt(token, confirmedAt);
    }

    @Override
    public ConfirmationToken save(ConfirmationToken token) {
        return confirmationTokenRepository.save(token);
    }
}
