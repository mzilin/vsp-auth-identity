package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.authservice.model.ResetToken;
import com.mariuszilinskas.vsp.authservice.repository.ResetTokenRepository;
import com.mariuszilinskas.vsp.authservice.util.AuthUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetTokenServiceImpl implements ResetTokenService {

    private static final Logger logger = LoggerFactory.getLogger(ResetTokenServiceImpl.class);

    private final ResetTokenRepository resetTokenRepository;
    private final TokenGenerationService tokenGenerationService;

    @Override
    @Transactional
    public String createResetToken(UUID userId) {
        logger.info("Creating Reset Token for User [userId: '{}']", userId);
        ResetToken resetToken = findOrCreateResetToken(userId);
        resetToken.setToken(tokenGenerationService.generateResetToken());
        resetToken.setExpiryDate(Instant.now().plusMillis(AuthUtils.FIFTEEN_MINS_IN_MILLIS));
        resetTokenRepository.save(resetToken);
        return resetToken.getToken();
    }

    private ResetToken findOrCreateResetToken(UUID userId) {
        return resetTokenRepository.findByUserId(userId)
                .orElse(new ResetToken(userId));
    }

    @Override
    @Transactional
    public ResetToken findResetToken(String token) {
        logger.info("Getting Reset Token [token: '{}']", token);
        return resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(ResetToken.class, "token", token));
    }

    @Override
    @Transactional
    public void deleteUserResetTokens(UUID userId) {
        logger.info("Deleting Reset Tokens for User [userId: '{}']", userId);
        resetTokenRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteExpiredResetTokens() {
        logger.info("Deleting Expired Reset Tokens");
        resetTokenRepository.deleteAllByExpiryDateBefore(Instant.now());
    }

}
