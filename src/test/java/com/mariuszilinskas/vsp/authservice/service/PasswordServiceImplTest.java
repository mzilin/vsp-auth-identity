package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.client.UserFeignClient;
import com.mariuszilinskas.vsp.authservice.dto.CreateCredentialsRequest;
import com.mariuszilinskas.vsp.authservice.dto.ForgotPasswordRequest;
import com.mariuszilinskas.vsp.authservice.dto.ResetPasswordRequest;
import com.mariuszilinskas.vsp.authservice.exception.*;
import com.mariuszilinskas.vsp.authservice.model.Password;
import com.mariuszilinskas.vsp.authservice.model.ResetToken;
import com.mariuszilinskas.vsp.authservice.repository.PasswordRepository;
import com.mariuszilinskas.vsp.authservice.util.AuthUtils;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordServiceImplTest {
    @Mock
    private ResetTokenServiceImpl resetTokenService;

    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private PasswordRepository passwordRepository;

    @InjectMocks
    PasswordServiceImpl passwordService;

    private FeignException feignException;
    private final UUID userId = UUID.randomUUID();
    private final String token = RandomStringUtils.randomAlphanumeric(20).toLowerCase();
    private final Password password = new Password(userId);
    private final ResetToken resetToken = new ResetToken(userId);

    // ------------------------------------

    @BeforeEach
    void setUp() {
        password.setPasswordHash("encodedPassword");
        resetToken.setToken(token);
        resetToken.setExpiryDate(Instant.now().plusMillis(AuthUtils.FIFTEEN_MINUTES));

        // Empty string for URL as a placeholder
        Request feignRequest = Request.create(
                Request.HttpMethod.POST,
                "", // Empty string for URL as a placeholder
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );

        feignException = new FeignException.NotFound(
                "Not found", feignRequest, null, Collections.emptyMap()
        );
    }

    // ------------------------------------

    @Test
    void testCreateNewPassword_Success() {
        // Arrange
        String email = "user@email.com";
        String newPassword = "Password1";
        ArgumentCaptor<Password> captor = ArgumentCaptor.forClass(Password.class);
        CreateCredentialsRequest request = new CreateCredentialsRequest(userId, email, newPassword);

        when(passwordRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn(password.getPasswordHash());
        when(passwordRepository.save(captor.capture())).thenReturn(password);

        // Act
        passwordService.createNewPassword(request);

        // Assert
        verify(passwordRepository, times(1)).findByUserId(userId);
        verify(bCryptPasswordEncoder, times(1)).encode(newPassword);
        verify(passwordRepository, times(1)).save(captor.capture());

        Password savedPassword = captor.getValue();
        assertEquals(password.getPasswordHash(), savedPassword.getPasswordHash());
    }

    // ------------------------------------

    @Test
    void testForgotPassword_Success() {
        // Arrange
        String email = "user@email.com";
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest(email);

        when(userFeignClient.getUserIdByEmail(email)).thenReturn(userId);
        when(resetTokenService.createResetToken(userId)).thenReturn(resetToken.getToken());

        // Act
        passwordService.forgotPassword(forgotPasswordRequest);

        // Assert
        verify(userFeignClient, times(1)).getUserIdByEmail(email);
        verify(resetTokenService, times(1)).createResetToken(userId);
    }

    @Test
    void testForgotPassword_FailsToFindUser() {
        // Arrange
        String email = "user@email.com";
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        doThrow(feignException).when(userFeignClient).getUserIdByEmail(email);

        // Act & Assert
        assertThrows(EmailVerificationException.class, () -> passwordService.forgotPassword(request));

        // Assert
        verify(userFeignClient, times(1)).getUserIdByEmail(email);
        verify(resetTokenService, never()).createResetToken(userId);
    }

    // ------------------------------------

    @Test
    void testResetPassword_Success() {
        // Arrange
        String newPassword = "Password1";
        String newPasswordHash = "HashedPassword";
        password.setPasswordHash(newPasswordHash);

        ArgumentCaptor<Password> captor = ArgumentCaptor.forClass(Password.class);
        ResetPasswordRequest request = new ResetPasswordRequest(newPassword, resetToken.getToken());

        when(resetTokenService.findResetToken(request.resetToken())).thenReturn(resetToken);
        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn(newPasswordHash);
        when(passwordRepository.save(captor.capture())).thenReturn(password);

        // Act
        passwordService.resetPassword(request);

        // Assert
        verify(resetTokenService, times(1)).findResetToken(resetToken.getToken());
        verify(bCryptPasswordEncoder, times(1)).encode(newPassword);
        verify(passwordRepository, times(1)).save(captor.capture());

        Password savedPassword = captor.getValue();
        assertEquals(newPasswordHash, savedPassword.getPasswordHash());
    }

    @Test
    void testResetPassword_ExpiredResetToken() {
        // Arrange
        String newPassword = "Password1";
        resetToken.setExpiryDate(Instant.now().minusSeconds(2));
        ResetPasswordRequest request = new ResetPasswordRequest(newPassword, resetToken.getToken());

        when(resetTokenService.findResetToken(request.resetToken())).thenReturn(resetToken);

        // Act & Assert
        assertThrows(ResetTokenValidationException.class, () -> passwordService.resetPassword(request));

        // Assert
        verify(resetTokenService, times(1)).findResetToken(resetToken.getToken());
        verify(bCryptPasswordEncoder, never()).encode(newPassword);
        verify(passwordRepository, never()).save(any(Password.class));
    }

    @Test
    void testResetPassword_IncorrectResetToken() {
        // Arrange
        String newPassword = "Password1";
        String incorrectToken = "incorrect_reset_token";
        ResetPasswordRequest request = new ResetPasswordRequest(newPassword, incorrectToken);

        when(resetTokenService.findResetToken(request.resetToken())).thenReturn(resetToken);

        // Act & Assert
        assertThrows(ResetTokenValidationException.class, () -> passwordService.resetPassword(request));

        // Assert
        verify(resetTokenService, times(1)).findResetToken(incorrectToken);
        verify(bCryptPasswordEncoder, never()).encode(newPassword);
        verify(passwordRepository, never()).save(any(Password.class));
    }

    @Test
    void testResetPassword_ResetTokenNotFound() {
        // Arrange
        String newPassword = "Password1";
        ResetPasswordRequest request = new ResetPasswordRequest(newPassword, resetToken.getToken());

        doThrow(ResourceNotFoundException.class).when(resetTokenService).findResetToken(request.resetToken());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> passwordService.resetPassword(request));

        // Assert
        verify(resetTokenService, times(1)).findResetToken(resetToken.getToken());
        verify(bCryptPasswordEncoder, never()).encode(newPassword);
        verify(passwordRepository, never()).save(any(Password.class));
    }

    // ------------------------------------

    @Test
    void testDeleteUserPasswords_Success() {
        // Arrange
        doNothing().when(passwordRepository).deleteByUserId(userId);
        when(passwordRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        passwordService.deleteUserPasswords(userId);

        // Assert
        verify(passwordRepository, times(1)).deleteByUserId(userId);
        assertFalse(passwordRepository.findByUserId(userId).isPresent());
    }

    @Test
    void testDeleteUserPasswords_NonExistingPassword() {
        // Arrange
        UUID nonExistentUserId = UUID.randomUUID();
        doNothing().when(passwordRepository).deleteByUserId(nonExistentUserId);
        when(passwordRepository.findByUserId(nonExistentUserId)).thenReturn(Optional.empty());

        // Act
        passwordService.deleteUserPasswords(nonExistentUserId);

        // Assert
        verify(passwordRepository, times(1)).deleteByUserId(nonExistentUserId);
        assertFalse(passwordRepository.findByUserId(nonExistentUserId).isPresent());
    }

    // ------------------------------------
}
