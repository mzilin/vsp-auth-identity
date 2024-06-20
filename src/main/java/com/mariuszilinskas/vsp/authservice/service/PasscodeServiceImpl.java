package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.client.UserFeignClient;
import com.mariuszilinskas.vsp.authservice.dto.VerifyPasscodeRequest;
import com.mariuszilinskas.vsp.authservice.exception.EmailVerificationException;
import com.mariuszilinskas.vsp.authservice.exception.PasscodeExpiredException;
import com.mariuszilinskas.vsp.authservice.exception.PasscodeValidationException;
import com.mariuszilinskas.vsp.authservice.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.authservice.model.Passcode;
import com.mariuszilinskas.vsp.authservice.repository.PasscodeRepository;
import com.mariuszilinskas.vsp.authservice.util.AuthUtils;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Service implementation for managing User Passcodes.
 *
 * @author Marius Zilinskas
 */
@Service
@RequiredArgsConstructor
public class PasscodeServiceImpl implements PasscodeService {

    private static final Logger logger = LoggerFactory.getLogger(PasscodeServiceImpl.class);
    private final UserFeignClient userFeignClient;
    private final PasscodeRepository passcodeRepository;
    private final TokenGenerationService tokenGenerationService;

    @Override
    @Transactional
    public void verifyPasscode(UUID userId, VerifyPasscodeRequest request) {
        logger.info("Verifying Passcode for User [userId: '{}']", userId);

        Passcode passcode = findPasscodeByUserId(userId);
        if (isPasscodeExpired(passcode)) {
            throw new PasscodeExpiredException();
        }

        if (!isPasscodeCorrect(passcode, request.passcode())) {
            throw new PasscodeValidationException();
        }

        verifyUserEmail(userId);
        deleteUserPasscodes(userId);

        // TODO: RabbitMQ - Send Welcome Email + TEST
    }

    private boolean isPasscodeExpired(Passcode passcode) {
        return passcode.getExpiryDate().isBefore(Instant.now());
    }

    private boolean isPasscodeCorrect(Passcode passcode, String givenPasscode) {
        return passcode.getPasscode().equals(givenPasscode);
    }

    private void verifyUserEmail(UUID userId) {
        try {
            userFeignClient.verifyUserEmail(userId);
        } catch (FeignException ex) {
            logger.error("Feign Exception when verifying User email: User ID '{}', Status {}, Body {}",
                    userId, ex.status(), ex.contentUTF8());
            throw new EmailVerificationException();
        }
    }

    @Override
    @Transactional
    public void resetPasscode(UUID userId) {
        logger.info("Resetting Passcode for User [userId: '{}']", userId);

        String passcode = createPasscode(userId);

        // TODO: RabbitMQ - Send verification Email + TEST
    }

    private String createPasscode(UUID userId) {
        Passcode passcode = findOrCreatePasscode(userId);
        passcode.setPasscode(tokenGenerationService.generatePasscode());
        passcode.setExpiryDate(Instant.now().plusMillis(AuthUtils.FIFTEEN_MINUTES_IN_MILLIS));
        passcodeRepository.save(passcode);
        return passcode.getPasscode();
    }

    private Passcode findOrCreatePasscode(UUID userId) {
        return passcodeRepository.findByUserId(userId)
                .orElse(new Passcode(userId));
    }

    private Passcode findPasscodeByUserId(UUID userId) {
        return passcodeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Passcode.class, "userId", userId));
    }

    @Override
    @Transactional
    public void deleteUserPasscodes(UUID userId) {
        logger.info("Deleting Passcodes for User [userId: '{}']", userId);
        passcodeRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteExpiredPasscodes() {
        logger.info("Deleting Expired Passcodes");
        passcodeRepository.deleteAllByExpiryDateBefore(Instant.now());
    }

}
