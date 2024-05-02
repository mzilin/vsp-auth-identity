package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.client.UserFeignClient;
import com.mariuszilinskas.vsp.authservice.dto.VerifyPasscodeRequest;
import com.mariuszilinskas.vsp.authservice.exception.EmailVerificationException;
import com.mariuszilinskas.vsp.authservice.exception.PasscodeExpiredException;
import com.mariuszilinskas.vsp.authservice.exception.PasscodeValidationException;
import com.mariuszilinskas.vsp.authservice.model.HashedPassword;
import com.mariuszilinskas.vsp.authservice.model.Passcode;
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

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasscodeServiceImplTest {

    @Mock
    private TokenGenerationService tokenGenerationService;

    @Mock
    private PasscodeRepository passcodeRepository;

    @Mock
    private UserFeignClient userFeignClient;

    @InjectMocks
    PasscodeServiceImpl passcodeService;

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
    void verifyPasscode_Success() {
        // Arrange
        VerifyPasscodeRequest request = new VerifyPasscodeRequest(passcode.getPasscode());

        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.of(passcode));
        when(userFeignClient.verifyUserEmail(userId)).thenReturn(null);

        doAnswer(invocation -> {
            when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.empty());
            return null;
        }).when(passcodeRepository).deleteByUserId(userId);

        // Act
        passcodeService.verifyPasscode(userId, request);

        // Assert
        verify(passcodeRepository, times(1)).findByUserId(userId);
        verify(userFeignClient, times(1)).verifyUserEmail(userId);
        verify(passcodeRepository, times(1)).deleteByUserId(userId);

        assertFalse(passcodeRepository.findByUserId(userId).isPresent());
    }

    @Test
    void verifyPasscode_ExpiredPasscode() {
        // Arrange
        passcode.setExpiryDate(Instant.now().minusSeconds(10));
        VerifyPasscodeRequest request = new VerifyPasscodeRequest(passcode.getPasscode());

        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.of(passcode));

        // Act & Assert
        assertThrows(PasscodeExpiredException.class, () -> passcodeService.verifyPasscode(userId, request));

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
        assertThrows(PasscodeValidationException.class, () -> passcodeService.verifyPasscode(userId, request));

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
        assertThrows(EmailVerificationException.class, () -> passcodeService.verifyPasscode(userId, request));

        // Assert
        verify(passcodeRepository, times(1)).findByUserId(userId);
        verify(userFeignClient, times(1)).verifyUserEmail(userId);
        verify(passcodeRepository, never()).deleteByUserId(userId);
    }

    // ------------------------------------

    @Test
    void resetPasscode_Success() {
        // Arrange
        String newPasscode = "abc123";
        passcode.setPasscode(newPasscode);
        ArgumentCaptor<Passcode> passcodeCaptor = ArgumentCaptor.forClass(Passcode.class);

        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.of(passcode));
        when(tokenGenerationService.generatePasscode()).thenReturn(newPasscode);
        when(passcodeRepository.save(passcodeCaptor.capture())).thenReturn(passcode);

        // Act
        passcodeService.resetPasscode(userId);

        // Assert
        verify(passcodeRepository, times(1)).findByUserId(userId);
        verify(passcodeRepository, times(1)).save(passcodeCaptor.capture());

        Passcode savedPasscode = passcodeCaptor.getValue();
        assertEquals(userId, savedPasscode.getUserId());
        assertEquals(newPasscode, savedPasscode.getPasscode());
    }

    // ------------------------------------

    @Test
    void deletePasscode_Success() {
        // Arrange
        doAnswer(invocation -> {
            when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.empty());
            return null;
        }).when(passcodeRepository).deleteByUserId(userId);

        // Act
        passcodeService.deletePasscode(userId);

        // Assert
        verify(passcodeRepository, times(1)).deleteByUserId(userId);

        assertFalse(passcodeRepository.findByUserId(userId).isPresent());
    }

    @Test
    void deletePasscode_NonExistingPasscode() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        passcodeService.deletePasscode(userId);

        // Assert
        verify(passcodeRepository, times(1)).deleteByUserId(userId);

        assertFalse(passcodeRepository.findByUserId(userId).isPresent());
    }
}
