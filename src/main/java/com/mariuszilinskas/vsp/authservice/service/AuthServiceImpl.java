package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.CredentialsRequest;
import com.mariuszilinskas.vsp.authservice.dto.LoginRequest;
import com.mariuszilinskas.vsp.authservice.exception.CredentialsValidationException;
import com.mariuszilinskas.vsp.authservice.exception.PasswordValidationException;
import com.mariuszilinskas.vsp.authservice.exception.ResourceNotFoundException;
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
        try {
            UUID userId = userService.getUserIdByEmail(request.email());
            passwordService.verifyPassword(new CredentialsRequest(userId, request.password()));
            generateAndSetAuthTokens(response, userId);
        } catch (PasswordValidationException | ResourceNotFoundException ex) {
            throw new CredentialsValidationException();
        }
    }

    private void generateAndSetAuthTokens(HttpServletResponse response, UUID userId) {
        UUID tokenId = UUID.randomUUID();
        refreshTokenService.createNewRefreshToken(tokenId, userId);
        jwtService.setAuthCookies(response, userId, tokenId);
    }

}
