package com.mariuszilinskas.vsp.authservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAuthDataServiceImplTest {

    @Mock
    private PasscodeService passcodeService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private ResetTokenService resetTokenService;

    @InjectMocks
    UserAuthDataServiceImpl userAuthDataService;

    private final UUID userId = UUID.randomUUID();

    // ------------------------------------

    @Test
    void testDeleteAllUserAuthData_Success() {
        // Arrange
        doNothing().when(passcodeService).deleteUserPasscodes(userId);
        doNothing().when(passwordService).deleteUserPasswords(userId);
        doNothing().when(resetTokenService).deleteUserResetTokens(userId);

        // Act
        userAuthDataService.deleteUserAuthData(userId);

        // Assert
        verify(passcodeService, times(1)).deleteUserPasscodes(userId);
        verify(passwordService, times(1)).deleteUserPasswords(userId);
        verify(resetTokenService, times(1)).deleteUserResetTokens(userId);
    }

}
