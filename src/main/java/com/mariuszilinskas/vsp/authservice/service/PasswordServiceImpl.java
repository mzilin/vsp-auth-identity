package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.client.UserFeignClient;
import com.mariuszilinskas.vsp.authservice.dto.CreateCredentialsRequest;
import com.mariuszilinskas.vsp.authservice.dto.ForgotPasswordRequest;
import com.mariuszilinskas.vsp.authservice.dto.ResetPasswordRequest;
import com.mariuszilinskas.vsp.authservice.exception.*;
import com.mariuszilinskas.vsp.authservice.model.Password;
import com.mariuszilinskas.vsp.authservice.model.ResetToken;
import com.mariuszilinskas.vsp.authservice.repository.PasswordRepository;
import feign.FeignException;
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

    private final UserFeignClient userFeignClient;
    private final PasswordRepository passwordRepository;
    private final ResetTokenService resetTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public void createNewPassword(CreateCredentialsRequest request) {
        logger.info("Creating Password for User [userId: '{}']", request.userId());
        createEncryptedPassword(request.userId(), request.password());
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        logger.info("Setting Password Reset Token for User [email: '{}']", request.email());

        UUID userId = getUserIdByEmail(request.email());
        String token = resetTokenService.createResetToken(userId);

        // TODO: Send Reset Password Email + TEST
    }

    private UUID getUserIdByEmail(String email) {
        try {
            return userFeignClient.getUserIdByEmail(email);
        } catch (FeignException ex) {
            logger.error("Failed to get User ID by Email [email: '{}']: {}", email, ex.getMessage());
            throw new EmailVerificationException();
        }
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
        Password password = findOrCreateHashedPassword(userId);
        setHashedPassword(password, newPassword);
    }

    private Password findOrCreateHashedPassword(UUID userId) {
        return passwordRepository.findByUserId(userId)
                .orElse(new Password(userId));
    }

    private void setHashedPassword(Password password, String newPassword) {
        password.setPasswordHash(bCryptPasswordEncoder.encode(newPassword));
        passwordRepository.save(password);
    }

    @Override
    @Transactional
    public void deleteUserPasswords(UUID userId) {
        logger.info("Deleting Passwords for User [userId: '{}']", userId);
        passwordRepository.deleteByUserId(userId);
    }

}
