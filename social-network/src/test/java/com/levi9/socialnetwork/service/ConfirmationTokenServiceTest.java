package com.levi9.socialnetwork.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import com.levi9.socialnetwork.Model.ConfirmationToken;
import com.levi9.socialnetwork.Repository.ConfirmationTokenRepository;
import com.levi9.socialnetwork.Service.impl.ConfirmationTokenServiceImpl;

@RunWith(SpringRunner.class)
public class ConfirmationTokenServiceTest {

    private static final LocalDateTime CONFIRMED_AT = LocalDateTime.of(2022, Month.NOVEMBER, 29, 19, 30, 40);
    private static final Long USER_ID = 1L;
    private static final String TOKEN = "dummy_token";
    
    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @InjectMocks
    private ConfirmationTokenServiceImpl confirmationTokenService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testFindByToken() {
        ConfirmationToken confirmationToken = new ConfirmationToken(TOKEN, CONFIRMED_AT, USER_ID);
        when(confirmationTokenRepository.findByToken(TOKEN)).thenReturn(confirmationToken);
        ConfirmationToken returnedToken = confirmationTokenService.findByToken(TOKEN);

        assertThat(returnedToken).usingRecursiveComparison().isEqualTo(confirmationToken);
        verify(confirmationTokenRepository, times(1)).findByToken(TOKEN);
        verifyNoMoreInteractions(confirmationTokenRepository);
    }

    @Test
    void testFindByUserId() {
        ConfirmationToken confirmationToken = new ConfirmationToken(TOKEN, CONFIRMED_AT, USER_ID);
        when(confirmationTokenRepository.findByUserId(USER_ID)).thenReturn(confirmationToken);
        ConfirmationToken returnedToken = confirmationTokenService.findByUserId(USER_ID);

        assertThat(returnedToken).usingRecursiveComparison().isEqualTo(confirmationToken);
        verify(confirmationTokenRepository, times(1)).findByUserId(USER_ID);
        verifyNoMoreInteractions(confirmationTokenRepository);
    }
    
    @Test
    void testUpdateConfirmedAt() {
        when(confirmationTokenRepository.updateConfirmedAt(TOKEN, CONFIRMED_AT)).thenReturn(1);
        
        int rowsAffected = confirmationTokenService.updateConfirmedAt(TOKEN, CONFIRMED_AT);
        
        assertThat(rowsAffected).isEqualTo(1);
        verify(confirmationTokenRepository, times(1)).updateConfirmedAt(TOKEN, CONFIRMED_AT);
        verifyNoMoreInteractions(confirmationTokenRepository);
    }
    
    @Test
    void testSave() {
        ConfirmationToken confirmationToken = new ConfirmationToken(TOKEN, CONFIRMED_AT, USER_ID);
        when(confirmationTokenRepository.save(confirmationToken)).thenReturn(confirmationToken);
        
        ConfirmationToken returnedToken = confirmationTokenService.save(confirmationToken);
        assertThat(returnedToken).usingRecursiveComparison().isEqualTo(confirmationToken);
        verify(confirmationTokenRepository, times(1)).save(confirmationToken);
        verifyNoMoreInteractions(confirmationTokenRepository);
    }
}
