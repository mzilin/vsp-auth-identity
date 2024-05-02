package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.CreateCredentialsRequest;
import com.mariuszilinskas.vsp.authservice.model.HashedPassword;
import com.mariuszilinskas.vsp.authservice.model.Passcode;
import com.mariuszilinskas.vsp.authservice.repository.HashedPasswordRepository;
import com.mariuszilinskas.vsp.authservice.repository.PasscodeRepository;
import com.mariuszilinskas.vsp.authservice.util.AuthUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final HashedPasswordRepository hashedPasswordRepository;
    private final PasscodeRepository passcodeRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenGenerationService tokenGenerationService;

    @Override
    @Transactional
    public void createPasswordAndSetPasscode(CreateCredentialsRequest request) {
        logger.info("Creating credentials for User [userId: '{}']", request.userId());

        createEncryptedPassword(request);
        String passcode = createPasscode(request.userId());

        // TODO: Send verification Email
    }

    private void createEncryptedPassword(CreateCredentialsRequest request) {
        HashedPassword password = findOrCreateHashedPassword(request.userId());
        password.setPasswordHash(bCryptPasswordEncoder.encode(request.password()));
        hashedPasswordRepository.save(password);
    }

    private String createPasscode(UUID userId) {
        Passcode passcode = findOrCreatePasscode(userId);
        passcode.setPasscode(tokenGenerationService.generatePasscode());
        passcode.setExpiryDate(Instant.now().plusMillis(AuthUtils.PIN_CODE_VALID_MILLIS));
        passcodeRepository.save(passcode);
        return passcode.getPasscode();
    }

    private HashedPassword findOrCreateHashedPassword(UUID userId) {
        return hashedPasswordRepository.findByUserId(userId)
                .orElse(new HashedPassword(userId));
    }

    private Passcode findOrCreatePasscode(UUID userId) {
        return passcodeRepository.findByUserId(userId)
                .orElse(new Passcode(userId));
    }

}
