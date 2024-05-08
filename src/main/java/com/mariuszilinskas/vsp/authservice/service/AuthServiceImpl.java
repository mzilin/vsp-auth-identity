package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.CreateCredentialsRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing User authentication.
 *
 * @author Marius Zilinskas
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final PasscodeService passcodeService;
    private final PasswordService passwordService;

    @Override
    @Transactional
    public void createPasswordAndSetPasscode(CreateCredentialsRequest request) {
        logger.info("Creating Credentials for User [userId: '{}']", request.userId());

        passwordService.createNewPassword(request);
        passcodeService.resetPasscode(request.userId());
    }

}
