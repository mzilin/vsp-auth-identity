package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.CreateCredentialsRequest;
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

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private HashedPasswordRepository hashedPasswordRepository;

    @Mock
    private TokenGenerationService tokenGenerationService;

    @Mock
    private PasscodeRepository passcodeRepository;

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
    void testCreatePasswordAndSetPasscode_newUserCredentials() {
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
    void testCreatePasswordAndSetPasscode_existingUserCredentials() {
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

}
