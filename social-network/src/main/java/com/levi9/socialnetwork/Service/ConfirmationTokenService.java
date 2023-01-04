package com.levi9.socialnetwork.Service;

import com.levi9.socialnetwork.Model.ConfirmationToken;

import java.time.LocalDateTime;

public interface ConfirmationTokenService {

    ConfirmationToken findByToken(String token);

    ConfirmationToken findByUserId(Long id);

    int updateConfirmedAt(String token, LocalDateTime confirmedAt);

    ConfirmationToken save(ConfirmationToken token);
}
