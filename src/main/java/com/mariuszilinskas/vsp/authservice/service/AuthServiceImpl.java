package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.AuthDetails;
import com.mariuszilinskas.vsp.authservice.dto.CredentialsRequest;
import com.mariuszilinskas.vsp.authservice.dto.LoginRequest;
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
        passcodeService.resetPasscode(request.userId());
    }

    @Override
    @Transactional
    public void authenticateUser(LoginRequest request, HttpServletResponse response) {
        logger.info("Authenticating User [email: {}]", request.email());
        AuthDetails authDetails;
        try {
            authDetails = userService.getUserAuthDetails(request.email());
        } catch (ResourceNotFoundException ex) {
            throw new CredentialsValidationException();
        }
        passwordService.verifyPassword(new CredentialsRequest(authDetails.userId(), request.password()));
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
        generateAndSetAuthTokens(response, refreshToken);
    }

    private void generateAndSetAuthTokens(HttpServletResponse response, AuthDetails authDetails) {
        createRefreshTokenAndSetAuthTokens(response, authDetails);
    }

    private void generateAndSetAuthTokens(HttpServletResponse response, String refreshToken) {
        AuthDetails authDetails = jwtService.extractAuthDetails(refreshToken, AuthUtils.REFRESH_TOKEN_NAME);
        UUID tokenId = jwtService.extractRefreshTokenId(refreshToken);
        createRefreshTokenAndSetAuthTokens(response, authDetails);
        refreshTokenService.deleteRefreshToken(tokenId);
    }

    private void createRefreshTokenAndSetAuthTokens(HttpServletResponse response, AuthDetails authDetails) {
        UUID tokenId = UUID.randomUUID();
        refreshTokenService.createNewRefreshToken(tokenId,  authDetails.userId());
        jwtService.setAuthCookies(response, authDetails, tokenId);
    }

}
