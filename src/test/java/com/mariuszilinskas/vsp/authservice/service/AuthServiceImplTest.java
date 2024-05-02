package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.client.UserFeignClient;
import com.mariuszilinskas.vsp.authservice.dto.CreateCredentialsRequest;
import com.mariuszilinskas.vsp.authservice.dto.VerifyPasscodeRequest;
import com.mariuszilinskas.vsp.authservice.exception.EmailVerificationException;
import com.mariuszilinskas.vsp.authservice.exception.PasscodeExpiredException;
import com.mariuszilinskas.vsp.authservice.exception.PasscodeValidationException;
import com.mariuszilinskas.vsp.authservice.model.HashedPassword;
import com.mariuszilinskas.vsp.authservice.model.Passcode;
import com.mariuszilinskas.vsp.authservice.repository.HashedPasswordRepository;
import com.mariuszilinskas.vsp.authservice.repository.PasscodeRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
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
public class AuthServiceImplTest {

    @Mock
    private TokenGenerationService tokenGenerationService;

    @Mock
    private PasscodeRepository passcodeRepository;

    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private HashedPasswordRepository hashedPasswordRepository;

    @InjectMocks
    AuthServiceImpl authService;

    private FeignException feignException;
    private final UUID userId = UUID.randomUUID();
    private final HashedPassword hashedPassword = new HashedPassword(userId);
    private final Passcode passcode = new Passcode(userId);

    // ------------------------------------

    @BeforeEach
    void setUp() {
        hashedPassword.setPasswordHash("encodedPassword");
        passcode.setPasscode("123456");
        passcode.setExpiryDate(Instant.now().plusSeconds(10));

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
    void testCreatePasswordAndSetPasscode_NewUserCredentials() {
        // Arrange
        CreateCredentialsRequest request = new CreateCredentialsRequest(userId, "user@example.com", "password123");

        ArgumentCaptor<HashedPassword> passwordCaptor = ArgumentCaptor.forClass(HashedPassword.class);
        ArgumentCaptor<Passcode> passcodeCaptor = ArgumentCaptor.forClass(Passcode.class);

        when(hashedPasswordRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(request.password())).thenReturn(hashedPassword.getPasswordHash());
        when(hashedPasswordRepository.save(passwordCaptor.capture())).thenReturn(hashedPassword);
        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(tokenGenerationService.generatePasscode()).thenReturn(passcode.getPasscode());
        when(passcodeRepository.save(passcodeCaptor.capture())).thenReturn(passcode);

        // Act
        authService.createPasswordAndSetPasscode(request);

        // Assert
        verify(bCryptPasswordEncoder, times(1)).encode(request.password());
        verify(hashedPasswordRepository, times(1)).findByUserId(userId);
        verify(hashedPasswordRepository, times(1)).save(passwordCaptor.capture());
        verify(passcodeRepository, times(1)).findByUserId(userId);
        verify(passcodeRepository, times(1)).save(passcodeCaptor.capture());

        HashedPassword savedPassword = passwordCaptor.getValue();
        assertEquals(userId, savedPassword.getUserId());
        assertEquals(hashedPassword.getPasswordHash(), savedPassword.getPasswordHash());

        Passcode savedPasscode = passcodeCaptor.getValue();
        assertEquals(userId, savedPasscode.getUserId());
        assertEquals(passcode.getPasscode(), savedPasscode.getPasscode());
    }

    @Test
    void testCreatePasswordAndSetPasscode_ExistingUserCredentials() {
        // Arrange
        UUID existingUserId = UUID.randomUUID();
        CreateCredentialsRequest request = new CreateCredentialsRequest(existingUserId, "user@example.com", "password123");
        hashedPassword.setUserId(existingUserId);
        passcode.setUserId(existingUserId);

        ArgumentCaptor<HashedPassword> passwordCaptor = ArgumentCaptor.forClass(HashedPassword.class);
        ArgumentCaptor<Passcode> passcodeCaptor = ArgumentCaptor.forClass(Passcode.class);

        when(hashedPasswordRepository.findByUserId(existingUserId)).thenReturn(Optional.of(hashedPassword));
        when(passcodeRepository.findByUserId(existingUserId)).thenReturn(Optional.of(passcode));
        when(passcodeRepository.save(passcodeCaptor.capture())).thenReturn(passcode);

        // Act
        authService.createPasswordAndSetPasscode(request);

        // Assert
        verify(bCryptPasswordEncoder, times(1)).encode(request.password());
        verify(hashedPasswordRepository, times(1)).save(passwordCaptor.capture());
        verify(passcodeRepository, times(1)).save(passcodeCaptor.capture());

        HashedPassword savedPassword = passwordCaptor.getValue();
        assertEquals(existingUserId, savedPassword.getUserId());
        assertEquals(hashedPassword.getPasswordHash(), savedPassword.getPasswordHash());

        Passcode savedPasscode = passcodeCaptor.getValue();
        assertEquals(existingUserId, savedPasscode.getUserId());
        assertEquals(passcode.getPasscode(), savedPasscode.getPasscode());
    }

    // ------------------------------------

    @Test
    void verifyPasscode_Success() {
        // Arrange
        VerifyPasscodeRequest request = new VerifyPasscodeRequest(passcode.getPasscode());

        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.of(passcode));
        when(userFeignClient.verifyUserEmail(userId)).thenReturn(null);
        doNothing().when(passcodeRepository).deleteByUserId(userId);

        // Act
        authService.verifyPasscode(userId, request);

        // Assert
        verify(passcodeRepository, times(1)).findByUserId(userId);
        verify(userFeignClient, times(1)).verifyUserEmail(userId);
        verify(passcodeRepository, times(1)).deleteByUserId(userId);
    }

    @Test
    void verifyPasscode_ExpiredPasscode() {
        // Arrange
        passcode.setExpiryDate(Instant.now().minusSeconds(10));
        VerifyPasscodeRequest request = new VerifyPasscodeRequest(passcode.getPasscode());

        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.of(passcode));

        // Act & Assert
        assertThrows(PasscodeExpiredException.class, () -> authService.verifyPasscode(userId, request));

        // Assert
        verify(passcodeRepository, times(1)).findByUserId(userId);
        verify(userFeignClient, never()).verifyUserEmail(userId);
        verify(passcodeRepository, never()).deleteByUserId(userId);
    }

    @Test
    void verifyPasscode_IncorrectPasscode() {
        // Arrange
        VerifyPasscodeRequest request = new VerifyPasscodeRequest("wrong1");

        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.of(passcode));

        // Act & Assert
        assertThrows(PasscodeValidationException.class, () -> authService.verifyPasscode(userId, request));

        // Assert
        verify(passcodeRepository, times(1)).findByUserId(userId);
        verify(userFeignClient, never()).verifyUserEmail(userId);
        verify(passcodeRepository, never()).deleteByUserId(userId);
    }

    @Test
    void verifyPasscode_FailsToVerifyEmail() {
        // Arrange
        VerifyPasscodeRequest request = new VerifyPasscodeRequest(passcode.getPasscode());

        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.of(passcode));
        doThrow(feignException).when(userFeignClient).verifyUserEmail(any(UUID.class));

        // Act & Assert
        assertThrows(EmailVerificationException.class, () -> authService.verifyPasscode(userId, request));

        // Assert
        verify(passcodeRepository, times(1)).findByUserId(userId);
        verify(userFeignClient, times(1)).verifyUserEmail(userId);
        verify(passcodeRepository, never()).deleteByUserId(userId);
    }

    // ------------------------------------

}
