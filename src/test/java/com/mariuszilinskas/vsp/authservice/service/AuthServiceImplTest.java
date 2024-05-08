package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.CreateCredentialsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private PasscodeService passcodeService;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    AuthServiceImpl authService;

    private final UUID userId = UUID.randomUUID();

    // ------------------------------------

    @BeforeEach
    void setUp() {
    }

    // ------------------------------------

    @Test
    void testCreatePasswordAndSetPasscode_NewUserCredentials() {
        // Arrange
        CreateCredentialsRequest request = new CreateCredentialsRequest(userId, "user@example.com", "password123");

        doNothing().when(passwordService).createNewPassword(request);
        doNothing().when(passcodeService).resetPasscode(userId);

        // Act
        authService.createPasswordAndSetPasscode(request);

        // Assert
        verify(passwordService, times(1)).createNewPassword(request);
        verify(passcodeService, times(1)).resetPasscode(userId);
    }

    // ------------------------------------

}
