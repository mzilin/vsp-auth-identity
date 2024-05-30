package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.client.UserFeignClient;
import com.mariuszilinskas.vsp.authservice.dto.CredentialsRequest;
import com.mariuszilinskas.vsp.authservice.exception.EmailVerificationException;
import com.mariuszilinskas.vsp.authservice.util.AuthTestUtils;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private PasscodeService passcodeService;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    AuthServiceImpl authService;

    private final UUID userId = UUID.randomUUID();
    private final FeignException feignException = AuthTestUtils.createFeignException();

    // ------------------------------------

    @BeforeEach
    void setUp() {}

    // ------------------------------------

    @Test
    void testCreatePasswordAndSetPasscode_NewUserCredentials() {
        // Arrange
        CredentialsRequest request = new CredentialsRequest(userId, "Password1!");

        doNothing().when(passwordService).createNewPassword(request);
        doNothing().when(passcodeService).resetPasscode(userId);

        // Act
        authService.createPasswordAndSetPasscode(request);

        // Assert
        verify(passwordService, times(1)).createNewPassword(request);
        verify(passcodeService, times(1)).resetPasscode(userId);
    }

    // ------------------------------------

    @Test
    void testGetUserIdByEmail_Success() {
        // Arrange
        String email = "some@email.com";
        when(userFeignClient.getUserIdByEmail(email)).thenReturn(userId);

        // Act
        authService.getUserIdByEmail(email);

        // Assert
        verify(userFeignClient, times(1)).getUserIdByEmail(email);
    }

    @Test
    void testGetUserIdByEmail_FeignException() {
        // Arrange
        String email = "some@email.com";
        doThrow(feignException).when(userFeignClient).getUserIdByEmail(email);

        // Act & Assert
        assertThrows(EmailVerificationException.class, () ->  authService.getUserIdByEmail(email));

        // Assert
        verify(userFeignClient, times(1)).getUserIdByEmail(email);
    }

    // ------------------------------------

}
