package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.authservice.model.RefreshToken;
import com.mariuszilinskas.vsp.authservice.repository.RefreshTokenRepository;
import com.mariuszilinskas.vsp.authservice.util.AuthUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Service implementation for managing User Refresh Tokens.
 *
 * @author Marius Zilinskas
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void createNewRefreshToken(UUID tokenId, UUID userId) {
        RefreshToken refreshToken = findOrCreateRefreshToken(tokenId, userId);
        refreshToken.setExpiryDate(Instant.now().plusMillis(AuthUtils.REFRESH_TOKEN_EXPIRATION));
        refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken findOrCreateRefreshToken(UUID tokenId, UUID userId) {
        return refreshTokenRepository.findByIdAndUserId(tokenId, userId)
                .orElse(new RefreshToken(tokenId, userId));
    }

    @Override
    public RefreshToken getRefreshToken(UUID tokenId) {
        return refreshTokenRepository.findById(tokenId)
                .orElseThrow(() -> new ResourceNotFoundException(RefreshToken.class, "id", tokenId));
    }

    @Override
    @Transactional
    public void deleteRefreshToken(UUID tokenId) {
        refreshTokenRepository.deleteById(tokenId);
        logger.info("Refresh token [id: '{}'] have been deleted", tokenId);
    }

    @Override
    @Transactional
    public void deleteUserRefreshTokens(UUID userId) {
        logger.info("Deleting Refresh Tokens for User [userId: '{}']", userId);
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteExpiredRefreshTokens() {
        logger.info("Deleting Expired Refresh Tokens");
        refreshTokenRepository.deleteAllByExpiryDateBefore(Instant.now());
    }
}
