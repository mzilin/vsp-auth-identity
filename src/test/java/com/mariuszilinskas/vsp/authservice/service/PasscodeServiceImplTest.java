package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.client.UserFeignClient;
import com.mariuszilinskas.vsp.authservice.dto.UserResponse;
import com.mariuszilinskas.vsp.authservice.dto.VerificationEmailRequest;
import com.mariuszilinskas.vsp.authservice.dto.VerifyPasscodeRequest;
import com.mariuszilinskas.vsp.authservice.dto.WelcomeEmailRequest;
import com.mariuszilinskas.vsp.authservice.exception.EmailVerificationException;
import com.mariuszilinskas.vsp.authservice.exception.PasscodeExpiredException;
import com.mariuszilinskas.vsp.authservice.exception.PasscodeValidationException;
import com.mariuszilinskas.vsp.authservice.exception.UserRetrievalException;
import com.mariuszilinskas.vsp.authservice.model.Passcode;
import com.mariuszilinskas.vsp.authservice.producer.RabbitMQProducer;
import com.mariuszilinskas.vsp.authservice.repository.PasscodeRepository;
import com.mariuszilinskas.vsp.authservice.util.TestUtils;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
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

    @Mock
    private RabbitMQProducer rabbitMQProducer;

    @InjectMocks
    private PasscodeServiceImpl passcodeService;

    private final UUID userId = UUID.randomUUID();
    String firstName = "firstName";
    private final String email = "user@email.com";
    private final Passcode passcode = new Passcode(userId);
    private final FeignException feignException = TestUtils.createFeignException();

    // ------------------------------------

    @BeforeEach
    void setUp() {
        passcode.setPasscode("123456");
        passcode.setExpiryDate(Instant.now().plusSeconds(10));
    }

    // ------------------------------------

    @Test
    void testVerifyPasscode_Success() {
        // Arrange
        var passcodeRequest = new VerifyPasscodeRequest(passcode.getPasscode());
        var emailRequest = new WelcomeEmailRequest("welcome", firstName, email);
        var userResponse = new UserResponse(firstName, "lastName", email);

        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.of(passcode));
        when(userFeignClient.getUser(userId)).thenReturn(userResponse);
        when(userFeignClient.verifyUserEmail(userId)).thenReturn(null);

        doAnswer(invocation -> {
            when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.empty());
            return null;
        }).when(passcodeRepository).deleteByUserId(userId);

        doNothing().when(rabbitMQProducer).sendWelcomeEmailMessage(emailRequest);

        // Act
        passcodeService.verifyPasscode(userId, passcodeRequest);

        // Assert
        verify(passcodeRepository, times(1)).findByUserId(userId);
        verify(userFeignClient, times(1)).getUser(userId);
        verify(userFeignClient, times(1)).verifyUserEmail(userId);
        verify(passcodeRepository, times(1)).deleteByUserId(userId);
        verify(rabbitMQProducer, times(1)).sendWelcomeEmailMessage(emailRequest);

        assertFalse(passcodeRepository.findByUserId(userId).isPresent());
    }

    @Test
    void testVerifyPasscode_ExpiredPasscode() {
        // Arrange
        passcode.setExpiryDate(Instant.now().minusSeconds(10));
        VerifyPasscodeRequest request = new VerifyPasscodeRequest(passcode.getPasscode());

        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.of(passcode));

        // Act & Assert
        assertThrows(PasscodeExpiredException.class, () -> passcodeService.verifyPasscode(userId, request));

        // Assert
        verify(passcodeRepository, times(1)).findByUserId(userId);

        verify(userFeignClient, never()).getUser(any(UUID.class));
        verify(userFeignClient, never()).verifyUserEmail(any(UUID.class));
        verify(passcodeRepository, never()).deleteByUserId(any(UUID.class));
        verify(rabbitMQProducer, never()).sendWelcomeEmailMessage(any(WelcomeEmailRequest.class));
    }

    @Test
    void testVerifyPasscode_IncorrectPasscode() {
        // Arrange
        VerifyPasscodeRequest request = new VerifyPasscodeRequest("wrong1");

        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.of(passcode));

        // Act & Assert
        assertThrows(PasscodeValidationException.class, () -> passcodeService.verifyPasscode(userId, request));

        // Assert
        verify(passcodeRepository, times(1)).findByUserId(userId);

        verify(userFeignClient, never()).getUser(any(UUID.class));
        verify(userFeignClient, never()).verifyUserEmail(any(UUID.class));
        verify(passcodeRepository, never()).deleteByUserId(any(UUID.class));
        verify(rabbitMQProducer, never()).sendWelcomeEmailMessage(any(WelcomeEmailRequest.class));
    }

    @Test
    void testVerifyPasscode_FailsToVerifyEmail() {
        // Arrange
        var userResponse = new UserResponse(firstName, "lastName", email);
        VerifyPasscodeRequest request = new VerifyPasscodeRequest(passcode.getPasscode());

        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.of(passcode));
        when(userFeignClient.getUser(userId)).thenReturn(userResponse);
        doThrow(feignException).when(userFeignClient).verifyUserEmail(userId);

        // Act & Assert
        assertThrows(EmailVerificationException.class, () -> passcodeService.verifyPasscode(userId, request));

        // Assert
        verify(passcodeRepository, times(1)).findByUserId(userId);
        verify(userFeignClient, times(1)).getUser(userId);
        verify(userFeignClient, times(1)).verifyUserEmail(userId);

        verify(passcodeRepository, never()).deleteByUserId(any(UUID.class));
        verify(rabbitMQProducer, never()).sendWelcomeEmailMessage(any(WelcomeEmailRequest.class));
    }

    @Test
    void testVerifyPasscode_FeignException() {
        // Arrange
        VerifyPasscodeRequest request = new VerifyPasscodeRequest(passcode.getPasscode());

        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.of(passcode));
        doThrow(feignException).when(userFeignClient).getUser(userId);

        // Act & Assert
        assertThrows(UserRetrievalException.class, () -> passcodeService.verifyPasscode(userId, request));

        // Assert
        verify(passcodeRepository, times(1)).findByUserId(userId);
        verify(userFeignClient, times(1)).getUser(userId);

        verify(userFeignClient, never()).verifyUserEmail(any(UUID.class));
        verify(passcodeRepository, never()).deleteByUserId(any(UUID.class));
        verify(rabbitMQProducer, never()).sendWelcomeEmailMessage(any(WelcomeEmailRequest.class));
    }

    // ------------------------------------

    @Test
    void testCreatePasscode_Success() {
        // Arrange
        String newPasscode = "abc123";
        passcode.setPasscode(newPasscode);
        ArgumentCaptor<Passcode> passcodeCaptor = ArgumentCaptor.forClass(Passcode.class);
        var userResponse = new UserResponse(firstName, "lastName", email);
        var emailRequest = new VerificationEmailRequest("verify", firstName, email, newPasscode);

        when(userFeignClient.getUser(userId)).thenReturn(userResponse);
        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.of(passcode));
        when(tokenGenerationService.generatePasscode()).thenReturn(newPasscode);
        when(passcodeRepository.save(passcodeCaptor.capture())).thenReturn(passcode);
        doNothing().when(rabbitMQProducer).sendVerificationEmailMessage(emailRequest);

        // Act
        passcodeService.createPasscode(userId, firstName, email);

        // Assert
        verify(userFeignClient, times(1)).getUser(userId);
        verify(passcodeRepository, times(1)).findByUserId(userId);
        verify(tokenGenerationService, times(1)).generatePasscode();
        verify(passcodeRepository, times(1)).save(passcodeCaptor.capture());
        verify(rabbitMQProducer, times(1)).sendVerificationEmailMessage(emailRequest);

        Passcode savedPasscode = passcodeCaptor.getValue();
        assertEquals(userId, savedPasscode.getUserId());
        assertEquals(newPasscode, savedPasscode.getPasscode());
    }

    @Test
    void testCreatePasscode_FeignException() {
        // Arrange
        doThrow(feignException).when(userFeignClient).getUser(userId);

        // Act & Assert
        assertThrows(UserRetrievalException.class, () -> passcodeService.createPasscode(userId, firstName, email));

        // Assert
        verify(userFeignClient, times(1)).getUser(userId);

        verify(passcodeRepository, never()).findByUserId(any(UUID.class));
        verify(tokenGenerationService, never()).generatePasscode();
        verify(passcodeRepository, never()).save(any(Passcode.class));
        verify(rabbitMQProducer, never()).sendVerificationEmailMessage(any(VerificationEmailRequest.class));
    }

    // ------------------------------------

    @Test
    void testResetPasscode_Success() {
        // Arrange
        String newPasscode = "abc123";
        passcode.setPasscode(newPasscode);
        ArgumentCaptor<Passcode> passcodeCaptor = ArgumentCaptor.forClass(Passcode.class);
        var userResponse = new UserResponse(firstName, "lastName", email);
        var emailRequest = new VerificationEmailRequest("verify", firstName, email, newPasscode);

        when(userFeignClient.getUser(userId)).thenReturn(userResponse);
        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.of(passcode));
        when(tokenGenerationService.generatePasscode()).thenReturn(newPasscode);
        when(passcodeRepository.save(passcodeCaptor.capture())).thenReturn(passcode);
        doNothing().when(rabbitMQProducer).sendVerificationEmailMessage(emailRequest);

        // Act
        passcodeService.resetPasscode(userId);

        // Assert
        verify(userFeignClient, times(1)).getUser(userId);
        verify(passcodeRepository, times(1)).findByUserId(userId);
        verify(tokenGenerationService, times(1)).generatePasscode();
        verify(passcodeRepository, times(1)).save(passcodeCaptor.capture());
        verify(rabbitMQProducer, times(1)).sendVerificationEmailMessage(emailRequest);

        Passcode savedPasscode = passcodeCaptor.getValue();
        assertEquals(userId, savedPasscode.getUserId());
        assertEquals(newPasscode, savedPasscode.getPasscode());
    }

    @Test
    void testResetPasscode_FeignException() {
        // Arrange
        doThrow(feignException).when(userFeignClient).getUser(userId);

        // Act & Assert
        assertThrows(UserRetrievalException.class, () -> passcodeService.resetPasscode(userId));

        // Assert
        verify(userFeignClient, times(1)).getUser(userId);

        verify(passcodeRepository, never()).findByUserId(any(UUID.class));
        verify(tokenGenerationService, never()).generatePasscode();
        verify(passcodeRepository, never()).save(any(Passcode.class));
        verify(rabbitMQProducer, never()).sendVerificationEmailMessage(any(VerificationEmailRequest.class));
    }

    // ------------------------------------

    @Test
    void testDeleteUserPasscodes_Success() {
        // Arrange
        doNothing().when(passcodeRepository).deleteByUserId(userId);
        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        passcodeService.deleteUserPasscodes(userId);

        // Assert
        verify(passcodeRepository, times(1)).deleteByUserId(userId);

        assertFalse(passcodeRepository.findByUserId(userId).isPresent());
    }

    @Test
    void testDeleteUserPasscodes_NonExistingPasscode() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(passcodeRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        passcodeService.deleteUserPasscodes(userId);

        // Assert
        verify(passcodeRepository, times(1)).deleteByUserId(userId);

        assertFalse(passcodeRepository.findByUserId(userId).isPresent());
    }

    // ------------------------------------

    @Test
    void testDeleteExpiredPasscodes_Success() {
        // Act
        passcodeService.deleteExpiredPasscodes();

        // Assert
        verify(passcodeRepository, times(1)).deleteAllByExpiryDateBefore(any(Instant.class));
    }

}
