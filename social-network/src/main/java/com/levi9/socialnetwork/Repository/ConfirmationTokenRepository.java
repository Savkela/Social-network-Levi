package com.levi9.socialnetwork.Repository;

import com.levi9.socialnetwork.Model.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    ConfirmationToken findByToken(String token);

    ConfirmationToken findByUserId(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken c " + "SET c.confirmedAt = ?2 " + "WHERE c.token = ?1")
    int updateConfirmedAt(String token, LocalDateTime confirmedAt);
}
