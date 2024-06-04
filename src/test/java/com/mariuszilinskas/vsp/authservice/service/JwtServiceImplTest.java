package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.AuthDetails;
import com.mariuszilinskas.vsp.authservice.exception.JwtTokenValidationException;
import com.mariuszilinskas.vsp.authservice.model.RefreshToken;
import com.mariuszilinskas.vsp.authservice.util.AuthTestUtils;
import com.mariuszilinskas.vsp.authservice.util.AuthUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceImplTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @InjectMocks
    private JwtServiceImpl jwtService;

    private static final String secretKey = AuthTestUtils.secretKey;
    private static final UUID userId = AuthTestUtils.userId;
    private static final UUID tokenId = AuthTestUtils.tokenId;
    private static final String validAccessToken = AuthTestUtils.validAccessToken;
    private static final String validRefreshToken = AuthTestUtils.validRefreshToken;
    private static final String expiredAccessToken = AuthTestUtils.expiredAccessToken;
    private static final String expiredRefreshToken = AuthTestUtils.expiredRefreshToken;
    private static final String invalidToken = AuthTestUtils.invalidToken;

    private AuthDetails authDetails;
    private final RefreshToken refreshToken = new RefreshToken();

    // ------------------------------------

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        setPrivateField(jwtService, "accessTokenSecret", secretKey);
        setPrivateField(jwtService, "refreshTokenSecret", secretKey);
        setPrivateField(jwtService, "environment", AuthUtils.PRODUCTION_ENV);

        authDetails = new AuthDetails(userId, List.of("USER"), List.of());

        refreshToken.setId(tokenId);
        refreshToken.setUserId(userId);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(10));

        mockRequest = mock(HttpServletRequest.class);
        mockResponse = new MockHttpServletResponse();
    }

    private void setPrivateField(Object targetObject, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = targetObject.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(targetObject, value);
    }

    // ------------------------------------

    @Test
    void testGenerateAccessToken() {
        // Act
        String token = jwtService.generateAccessToken(authDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    // ------------------------------------

    @Test
    void testGenerateRefreshToken() {
        // Act
        String token = jwtService.generateRefreshToken(userId, UUID.randomUUID());

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    // ------------------------------------

    @Test
    void testSetAuthCookies() {
        // Act
        jwtService.setAuthCookies(mockResponse, authDetails, tokenId);

        // Retrieve all cookies set on the response
        Collection<String> setCookieHeaders = ((MockHttpServletResponse) mockResponse).getHeaders("Set-Cookie");
        boolean accessTokenFound = false, refreshTokenFound = false;

        // Assert
        for (String header : setCookieHeaders) {
            if (header.contains(AuthUtils.ACCESS_TOKEN_NAME)) {
                accessTokenFound = true;
                assertTrue(header.contains("HttpOnly"));
                assertTrue(header.contains("Path=/"));
                assertTrue(header.contains("Secure"));
                assertTrue(header.contains("SameSite=None"));
            }
            if (header.contains(AuthUtils.REFRESH_TOKEN_NAME)) {
                refreshTokenFound = true;
                assertTrue(header.contains("HttpOnly"));
                assertTrue(header.contains("Path=/"));
                assertTrue(header.contains("Secure"));
                assertTrue(header.contains("SameSite=None"));
            }
        }

        assertTrue(accessTokenFound);
        assertTrue(refreshTokenFound);
    }

    // ------------------------------------

    @Test
    void testClearAuthCookies() {
        // Act
        jwtService.clearAuthCookies(mockResponse);

        // Retrieve all cookies set on the response
        Collection<String> setCookieHeaders = ((MockHttpServletResponse) mockResponse).getHeaders("Set-Cookie");
        boolean accessTokenFound = false, refreshTokenFound = false;

        // Assert
        for (String header : setCookieHeaders) {
            if (header.contains(AuthUtils.ACCESS_TOKEN_NAME)) {
                accessTokenFound = true;
                assertTrue(header.contains("HttpOnly"));
                assertTrue(header.contains("Path=/"));
                assertTrue(header.contains("Max-Age=0"));
                assertTrue(header.contains("Secure"));
            }
            if (header.contains(AuthUtils.REFRESH_TOKEN_NAME)) {
                refreshTokenFound = true;
                assertTrue(header.contains("HttpOnly"));
                assertTrue(header.contains("Path=/"));
                assertTrue(header.contains("Max-Age=0"));
                assertTrue(header.contains("Secure"));
            }
        }

        assertTrue(accessTokenFound);
        assertTrue(refreshTokenFound);
    }

    // ------------------------------------

    @Test
    void testExtractAccessToken_Success() {
        // Arrange
        Cookie[] cookies = {new Cookie(AuthUtils.ACCESS_TOKEN_NAME, "sampleToken")};
        when(mockRequest.getCookies()).thenReturn(cookies);

        // Act
        String token = jwtService.extractAccessToken(mockRequest);

        // Assert
        assertEquals("sampleToken", token);
    }

    @Test
    void testExtractAccessToken_NoCookies() {
        // Arrange
        when(mockRequest.getCookies()).thenReturn(null);

        // Act
        String token = jwtService.extractAccessToken(mockRequest);

        // Assert
        assertNull(token);
    }

    // ------------------------------------

    @Test
    void testExtractRefreshToken_Success() {
        // Arrange
        Cookie[] cookies = {new Cookie(AuthUtils.REFRESH_TOKEN_NAME, "sampleToken")};
        when(mockRequest.getCookies()).thenReturn(cookies);

        // Act
        String token = jwtService.extractRefreshToken(mockRequest);

        // Assert
        assertEquals("sampleToken", token);
    }

    @Test
    void testExtractRefreshToken_NoCookies() {
        // Arrange
        when(mockRequest.getCookies()).thenReturn(null);

        // Act
        String token = jwtService.extractRefreshToken(mockRequest);

        // Assert
        assertNull(token);
    }

    // ------------------------------------

    @Test
    void testValidateAccessToken_WithValidToken_DoesNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> jwtService.validateAccessToken(validAccessToken));
    }

    @Test
    void testValidateAccessToken_WithInvalidToken_ThrowsJwtTokenValidationException() {
        // Act & Assert
        assertThrows(JwtTokenValidationException.class, () -> jwtService.validateAccessToken(invalidToken));
    }

    @Test
    void testValidateAccessToken_WithExpiredToken_ThrowsJwtTokenValidationException() {
        // Act & Assert
        assertThrows(JwtTokenValidationException.class, () -> jwtService.validateAccessToken(expiredAccessToken));
    }

    // ------------------------------------

    @Test
    void testValidateRefreshToken_WithValidToken_DoesNotThrowException() {
        // Arrange
        when(refreshTokenService.getRefreshToken(tokenId)).thenReturn(refreshToken);

        // Act & Assert
        assertDoesNotThrow(() -> jwtService.validateRefreshToken(validRefreshToken));

        verify(refreshTokenService, times(1)).getRefreshToken(tokenId);
    }

    @Test
    void testValidateRefreshToken_WithInvalidToken_ThrowsJwtTokenValidationException() {
        // Act & Assert
        assertThrows(JwtTokenValidationException.class, () -> jwtService.validateRefreshToken(invalidToken));
    }

    @Test
    void testValidateRefreshToken_WithExpiredToken_ThrowsJwtTokenValidationException() {
        // Act & Assert
        assertThrows(JwtTokenValidationException.class, () -> jwtService.validateRefreshToken(expiredRefreshToken));
    }

    @Test
    void testValidateRefreshToken_WithTokenNotInDatabase_ThrowsJwtTokenValidationException() {
        // Arrange
        when(refreshTokenService.getRefreshToken(tokenId)).thenReturn(null);
        doNothing().when(refreshTokenService).deleteUserRefreshTokens(userId);

        // Act & Assert
        assertThrows(JwtTokenValidationException.class, () -> jwtService.validateRefreshToken(validRefreshToken));

        verify(refreshTokenService, times(1)).getRefreshToken(tokenId);
        verify(refreshTokenService, times(1)).deleteUserRefreshTokens(userId);
    }

    @Test
    void testValidateRefreshToken_WithExpiredTokenInDatabase_ThrowsJwtTokenValidationException() {
        // Arrange
        refreshToken.setExpiryDate(Instant.now().minusMillis(3600));
        when(refreshTokenService.getRefreshToken(tokenId)).thenReturn(refreshToken);
        doNothing().when(refreshTokenService).deleteRefreshToken(tokenId);

        // Act & Assert
        assertThrows(JwtTokenValidationException.class, () -> jwtService.validateRefreshToken(validRefreshToken));

        verify(refreshTokenService, times(1)).getRefreshToken(tokenId);
        verify(refreshTokenService, times(1)).deleteRefreshToken(tokenId);
        verify(refreshTokenService, never()).deleteUserRefreshTokens(userId);
    }

    // ------------------------------------

    @Test
    void testExtractUserIdFromToken_validAccessToken() {
        // Act
        UUID response = jwtService.extractUserIdFromToken(validAccessToken, AuthUtils.ACCESS_TOKEN_NAME);

        // Assert
        assertEquals(userId, response);
    }

    @Test
    void testExtractUserIdFromToken_invalidAccessToken() {
        // Act & Assert
        assertThrows(JwtTokenValidationException.class, () -> {
            jwtService.extractUserIdFromToken(invalidToken, AuthUtils.ACCESS_TOKEN_NAME);
        });
    }

    @Test
    void testExtractUserIdFromToken_validRefreshToken() {
        // Act
        UUID response = jwtService.extractUserIdFromToken(validRefreshToken, AuthUtils.REFRESH_TOKEN_NAME);

        // Assert
        assertEquals(userId, response);
    }

    @Test
    void testExtractUserIdFromToken_invalidRefreshToken() {
        // Act & Assert
        assertThrows(JwtTokenValidationException.class, () -> {
            jwtService.extractUserIdFromToken(invalidToken, AuthUtils.REFRESH_TOKEN_NAME);
        });
    }

    // ------------------------------------

    @Test
    void testExtractRefreshTokenId_validToken() {
        // Act
        UUID refreshTokenId = jwtService.extractRefreshTokenId(validRefreshToken);

        // Assert
        assertEquals(tokenId, refreshTokenId);
    }

    @Test
    void testExtractRefreshTokenId_invalidToken() {
        // Act & Assert
        assertThrows(JwtTokenValidationException.class, () -> jwtService.extractRefreshTokenId(invalidToken));
    }

}
