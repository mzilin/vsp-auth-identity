package com.mariuszilinskas.vsp.authservice.service;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class TokenGenerationService {

    public String generatePasscode() {
        String allowedChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Excludes 0, O, I and 1
        return RandomStringUtils.random(6, allowedChars).toUpperCase();
    }

    public String generateResetToken() {
        return RandomStringUtils.randomAlphanumeric(20).toLowerCase();
    }
}
