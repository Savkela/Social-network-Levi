package com.levi9.socialnetwork.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "confirmation_token", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class ConfirmationToken {

    @Id
    @Column(nullable = false)
    private String token;

    @Column(nullable = true, name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "id_user")
    private Long userId;

    public ConfirmationToken(String token, LocalDateTime confirmedAt, Long userId) {
        this.token = token;
        this.confirmedAt = confirmedAt;
        this.userId = userId;
    }
}
