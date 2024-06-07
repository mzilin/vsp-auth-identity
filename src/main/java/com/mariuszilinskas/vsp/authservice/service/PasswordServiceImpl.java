package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.AuthDetails;
import com.mariuszilinskas.vsp.authservice.dto.CredentialsRequest;
import com.mariuszilinskas.vsp.authservice.dto.ForgotPasswordRequest;
import com.mariuszilinskas.vsp.authservice.dto.ResetPasswordRequest;
import com.mariuszilinskas.vsp.authservice.enums.UserStatus;
import com.mariuszilinskas.vsp.authservice.exception.*;
import com.mariuszilinskas.vsp.authservice.model.Password;
import com.mariuszilinskas.vsp.authservice.model.ResetToken;
import com.mariuszilinskas.vsp.authservice.repository.PasswordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Service implementation for managing User Passwords.
 *
 * @author Marius Zilinskas
 */
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordServiceImpl.class);
    private final UserService userService;
    private final PasswordRepository passwordRepository;
    private final ResetTokenService resetTokenService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createNewPassword(CredentialsRequest request) {
        logger.info("Creating Password for User [userId: '{}']", request.userId());
        createEncryptedPassword(request.userId(), request.password());
    }

    @Override
    public void verifyPassword(CredentialsRequest request) {
        logger.info("Verifying Password for User [userId: '{}']", request.userId());
        Password storedPassword = getPasswordByUserId(request.userId());
        validatePassword(request.password(), storedPassword);
    }

    private Password getPasswordByUserId(UUID userId) {
        return passwordRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Password.class, "userId", userId));
    }
    private void validatePassword(String providedPassword, Password storedPassword) {
        if (!passwordEncoder.matches(providedPassword, storedPassword.getPasswordHash()))
            throw new CredentialsValidationException();
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        logger.info("Setting Password Reset Token for User [email: '{}']", request.email());

        AuthDetails authDetails = userService.getUserAuthDetailsWithEmail(request.email());
        checkUserActive(authDetails.status());

        String token = resetTokenService.createResetToken(authDetails.userId());

        // TODO: RabbitMQ - Send Reset Password Email + TEST
    }

    private void checkUserActive(UserStatus status) {
        if (UserStatus.ACTIVE != status)
            throw new UserStatusAccessException(status.toString());
    }


    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        ResetToken resetToken = resetTokenService.findResetToken(request.resetToken());
        logger.info("Resetting New Password for User [userId: '{}']", resetToken.getUserId());

        validateResetToken(resetToken, request.resetToken());
        createEncryptedPassword(resetToken.getUserId(), request.password());
    }

    private void validateResetToken(ResetToken resetToken, String givenResetToken) {
        if (isResetTokenExpired(resetToken) || !isResetTokenCorrect(resetToken, givenResetToken)) {
            throw new ResetTokenValidationException();
        }
    }

    private boolean isResetTokenCorrect(ResetToken resetToken, String givenResetToken) {
        return resetToken.getToken().equals(givenResetToken);
    }

    private boolean isResetTokenExpired(ResetToken resetToken) {
        return resetToken.getExpiryDate().isBefore(Instant.now());
    }

    private void createEncryptedPassword(UUID userId, String newPassword) {
        Password password = getOrCreateHashedPassword(userId);
        setHashedPassword(password, newPassword);
    }

    private Password getOrCreateHashedPassword(UUID userId) {
        return passwordRepository.findByUserId(userId)
                .orElse(new Password(userId));
    }

    private void setHashedPassword(Password password, String newPassword) {
        password.setPasswordHash(passwordEncoder.encode(newPassword));
        passwordRepository.save(password);
    }

    @Override
    @Transactional
    public void deleteUserPasswords(UUID userId) {
        logger.info("Deleting Passwords for User [userId: '{}']", userId);
        passwordRepository.deleteByUserId(userId);
    }

}
