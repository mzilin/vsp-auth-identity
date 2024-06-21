package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.*;
import com.mariuszilinskas.vsp.authservice.exception.*;
import com.mariuszilinskas.vsp.authservice.util.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Service implementation for managing User authentication.
 *
 * @author Marius Zilinskas
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final JwtService jwtService;
    private final PasscodeService passcodeService;
    private final PasswordService passwordService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    @Override
    @Transactional
    public void createPasswordAndSetPasscode(CredentialsRequest request) {
        logger.info("Creating Credentials for User [userId: '{}']", request.userId());
        passwordService.createNewPassword(request);
        passcodeService.resetPasscode(new ResetPasscodeRequest(request.userId(), request.email()));
    }

    @Override
    @Transactional
    public void authenticateUser(LoginRequest request, HttpServletResponse response) {
        logger.info("Authenticating User [email: {}]", request.email());

        AuthDetails authDetails = fetchAuthDetails(() -> userService.getUserAuthDetailsWithEmail(request.email()));
        AuthUtils.checkUserSuspended(authDetails.status());
        passwordService.verifyPassword(new VerifyPasswordRequest(authDetails.userId(), request.password()));

        generateAndSetAuthTokens(response, authDetails);
    }

    @Override
    @Transactional
    public void refreshTokens(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Refreshing auth tokens");
        String refreshToken = jwtService.extractRefreshToken(request);

        if (refreshToken == null || refreshToken.isEmpty()) {
            logger.error("Refresh token is null");
            throw new SessionExpiredException();
        }

        jwtService.validateRefreshToken(refreshToken);
        UUID userId = jwtService.extractUserIdFromToken(refreshToken, AuthUtils.REFRESH_TOKEN_NAME);

        AuthDetails authDetails = fetchAuthDetails(() -> userService.getUserAuthDetailsWithId(userId));
        AuthUtils.checkUserSuspended(authDetails.status());

        UUID tokenId = jwtService.extractRefreshTokenId(refreshToken);
        generateAndSetAuthTokens(response, authDetails);

        refreshTokenService.deleteRefreshToken(tokenId);
    }

    private AuthDetails fetchAuthDetails(Supplier<AuthDetails> supplier) {
        try {
            return supplier.get();
        } catch (ResourceNotFoundException ex) {
            throw new CredentialsValidationException();
        }
    }

    private void generateAndSetAuthTokens(HttpServletResponse response, AuthDetails authDetails) {
        UUID tokenId = UUID.randomUUID();
        refreshTokenService.createNewRefreshToken(tokenId, authDetails.userId());
        jwtService.setAuthCookies(response, authDetails, tokenId);
    }

    @Override
    public void logoutUser(HttpServletRequest request, HttpServletResponse response, UUID userId) {
        logger.info("Logging out User [userId: {}]", userId);
        try {
            String refreshToken = jwtService.extractRefreshToken(request);
            if (refreshToken != null && !refreshToken.isEmpty()) {
                UUID tokenId = jwtService.extractRefreshTokenId(refreshToken);
                refreshTokenService.deleteRefreshToken(tokenId);
            }
        } finally {
            jwtService.clearAuthCookies(response);
        }
    }

}
