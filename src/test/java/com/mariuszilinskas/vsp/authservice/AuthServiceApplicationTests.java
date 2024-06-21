package com.mariuszilinskas.vsp.authservice;

import com.mariuszilinskas.vsp.authservice.client.UserFeignClient;
import com.mariuszilinskas.vsp.authservice.config.FeignConfig;
import com.mariuszilinskas.vsp.authservice.config.RabbitMQConfig;
import com.mariuszilinskas.vsp.authservice.controller.AuthController;
import com.mariuszilinskas.vsp.authservice.controller.PasscodeController;
import com.mariuszilinskas.vsp.authservice.controller.PasswordController;
import com.mariuszilinskas.vsp.authservice.controller.DataDeletionController;
import com.mariuszilinskas.vsp.authservice.producer.RabbitMQProducer;
import com.mariuszilinskas.vsp.authservice.repository.PasscodeRepository;
import com.mariuszilinskas.vsp.authservice.repository.PasswordRepository;
import com.mariuszilinskas.vsp.authservice.repository.RefreshTokenRepository;
import com.mariuszilinskas.vsp.authservice.repository.ResetTokenRepository;
import com.mariuszilinskas.vsp.authservice.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for the Spring application context and bean configuration in the AuthService application.
 */
@SpringBootTest
class AuthServiceApplicationTests {

    // ------------------------ Services ----------------------------

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private DataDeletionServiceImpl dataDeletionService;

    @Autowired
    private JwtServiceImpl jwtService;

    @Autowired
    private PasscodeServiceImpl passcodeService;

    @Autowired
    private PasswordServiceImpl passwordService;

    @Autowired
    private RefreshTokenServiceImpl refreshTokenService;

    @Autowired
    private ResetTokenServiceImpl resetTokenService;

    @Autowired
    private TokenGenerationService tokenGenerationService;

    @Autowired
    private UserServiceImpl userService;

    // ---------------------- Repositories --------------------------

    @Autowired
    private PasscodeRepository passcodeRepository;

    @Autowired
    private PasswordRepository passwordRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    // ----------------------- Controllers --------------------------

    @Autowired
    private AuthController authController;

    @Autowired
    private DataDeletionController dataDeletionController;

    @Autowired
    private PasscodeController passcodeController;

    @Autowired
    private PasswordController passwordController;

    // ------------------------- Other ------------------------------

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private FeignConfig feignConfig;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    // --------------------------------------------------------------

    @Test
    void contextLoads() {
    }

    // ------------------------ Services ----------------------------

    @Test
    void authServiceBeanLoads() {
        assertNotNull(authService, "Auth Service should have been auto-wired by Spring Context");
    }

    @Test
    void dataDeletionServiceBeanLoads() {
        assertNotNull(dataDeletionService, "Data Deletion Service should have been auto-wired by Spring Context");
    }

    @Test
    void jwtServiceBeanLoads() {
        assertNotNull(jwtService, "Jwt Service should have been auto-wired by Spring Context");
    }

    @Test
    void passcodeServiceBeanLoads() {
        assertNotNull(passcodeService, "Passcode Service should have been auto-wired by Spring Context");
    }

    @Test
    void passwordServiceBeanLoads() {
        assertNotNull(passwordService, "Password Service should have been auto-wired by Spring Context");
    }

    @Test
    void refreshTokenServiceBeanLoads() {
        assertNotNull(refreshTokenService, "Refresh Token Service should have been auto-wired by Spring Context");
    }

    @Test
    void resetTokenServiceBeanLoads() {
        assertNotNull(resetTokenService, "Reset Token Service should have been auto-wired by Spring Context");
    }

    @Test
    void tokenGenerationServiceBeanLoads() {
        assertNotNull(tokenGenerationService, "Token Generation Service should have been auto-wired by Spring Context");
    }

    @Test
    void userServiceBeanLoads() {
        assertNotNull(userService, "User Service should have been auto-wired by Spring Context");
    }

    // ---------------------- Repositories --------------------------

    @Test
    void passcodeRepositoryBeanLoads() {
        assertNotNull(passcodeRepository, "Passcode Repository should have been auto-wired by Spring Context");
    }

    @Test
    void passwordRepositoryBeanLoads() {
        assertNotNull(passwordRepository, "Password Repository should have been auto-wired by Spring Context");
    }

    @Test
    void refreshTokenRepositoryBeanLoads() {
        assertNotNull(refreshTokenRepository, "Refresh Token Repository should have been auto-wired by Spring Context");
    }

    @Test
    void resetTokenRepositoryBeanLoads() {
        assertNotNull(resetTokenRepository, "Reset Token Repository should have been auto-wired by Spring Context");
    }

    // ----------------------- Controllers --------------------------

    @Test
    void authControllerBeanLoads() {
        assertNotNull(authController, "Auth Controller should have been auto-wired by Spring Context");
    }

    @Test
    void dataDeletionControllerBeanLoads() {
        assertNotNull(dataDeletionController, "Data Deletion Controller should have been auto-wired by Spring Context");
    }

    @Test
    void passcodeControllerBeanLoads() {
        assertNotNull(passcodeController, "Passcode Controller should have been auto-wired by Spring Context");
    }

    @Test
    void passwordControllerBeanLoads() {
        assertNotNull(passwordController, "Password Controller should have been auto-wired by Spring Context");
    }

    // ------------------------- Other ------------------------------

    @Test
    void passwordEncoderBeanLoads() {
        assertNotNull(passwordEncoder, "Password Encoder should have been auto-wired by Spring Context");
    }

    @Test
    void userFeignClientBeanLoads() {
        assertNotNull(userFeignClient, "User Feign Client should have been auto-wired by Spring Context");
    }

    @Test
    void feignConfigBeanLoads() {
        assertNotNull(feignConfig, "Feign Config should have been auto-wired by Spring Context");
    }

    @Test
    void rabbitMQConfigBeanLoads() {
        assertNotNull(rabbitMQConfig, "RabbitMQ Config should have been auto-wired by Spring Context");
    }

    @Test
    void rabbitMQProducerBeanLoads() {
        assertNotNull(rabbitMQProducer, "RabbitMQ Producer should have been auto-wired by Spring Context");
    }

}
