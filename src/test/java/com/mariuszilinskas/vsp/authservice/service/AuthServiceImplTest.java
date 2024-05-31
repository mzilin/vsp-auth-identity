package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.CredentialsRequest;
import com.mariuszilinskas.vsp.authservice.dto.LoginRequest;
import com.mariuszilinskas.vsp.authservice.exception.CredentialsValidationException;
import com.mariuszilinskas.vsp.authservice.exception.PasswordValidationException;
import com.mariuszilinskas.vsp.authservice.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.authservice.util.AuthTestUtils;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private PasscodeService passcodeService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @InjectMocks
    AuthServiceImpl authService;

    private final UUID userId = UUID.randomUUID();
    private final FeignException feignException = AuthTestUtils.createFeignException();

    // ------------------------------------

    @BeforeEach
    void setUp() {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
    }

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
    void testAuthenticateUser_Success() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("some@email.com", "Password1!");
        CredentialsRequest credentialsRequest = new CredentialsRequest(userId, loginRequest.password());

        when(userDetailsService.getUserIdByEmail(loginRequest.email())).thenReturn(userId);
        doNothing().when(passwordService).verifyPassword(credentialsRequest);
        doNothing().when(refreshTokenService).createNewRefreshToken(any(UUID.class), eq(userId));
        doNothing().when(jwtService).setAuthCookies(eq(mockResponse), eq(userId), any(UUID.class));

        // Act
        authService.authenticateUser(loginRequest, mockResponse);

        // Assert
        verify(userDetailsService, times(1)).getUserIdByEmail(loginRequest.email());
        verify(passwordService, times(1)).verifyPassword(credentialsRequest);
        verify(refreshTokenService, times(1)).createNewRefreshToken(any(UUID.class), eq(userId));
        verify(jwtService, times(1)).setAuthCookies(eq(mockResponse), eq(userId), any(UUID.class));
    }

    @Test
    void testAuthenticateUser_InvalidCredentials() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@email.com", "wrongPassword");
        CredentialsRequest credentialsRequest = new CredentialsRequest(userId, loginRequest.password());

        when(userDetailsService.getUserIdByEmail(loginRequest.email())).thenReturn(userId);
        doThrow(PasswordValidationException.class).when(passwordService).verifyPassword(credentialsRequest);

        // Act & Assert
        assertThrows(CredentialsValidationException.class, () -> {
            authService.authenticateUser(loginRequest, mockResponse);
        });

        // Assert
        verify(userDetailsService, times(1)).getUserIdByEmail(loginRequest.email());
        verify(passwordService, times(1)).verifyPassword(credentialsRequest);
        verify(refreshTokenService, never()).createNewRefreshToken(any(UUID.class), eq(userId));
        verify(jwtService, never()).setAuthCookies(any(HttpServletResponse.class), any(UUID.class), any(UUID.class));
    }

    @Test
    void testAuthenticateUser_NonExistingUser() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("some@email.com", "Password1!");
        when(userDetailsService.getUserIdByEmail(loginRequest.email())).thenThrow(ResourceNotFoundException.class);

        // Act & Assert
        assertThrows(CredentialsValidationException.class, () -> {
            authService.authenticateUser(loginRequest, mockResponse);
        });

        // Assert
        verify(userDetailsService, times(1)).getUserIdByEmail(loginRequest.email());
        verify(passwordService, never()).verifyPassword(any(CredentialsRequest.class));
        verify(refreshTokenService, never()).createNewRefreshToken(any(UUID.class), any(UUID.class));
        verify(jwtService, never()).setAuthCookies(any(HttpServletResponse.class), any(UUID.class), any(UUID.class));
    }

    // ------------------------------------


}
