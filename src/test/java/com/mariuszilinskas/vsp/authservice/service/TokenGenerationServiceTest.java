package com.mariuszilinskas.vsp.authservice.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TokenGenerationServiceTest {

    private final TokenGenerationService tokenGenerationService = new TokenGenerationService();

    // ------------------------------------

    @Test
    void testGeneratePasscode_ReturnsNonNullString() {
        String passcode = tokenGenerationService.generatePasscode();
        assertNotNull(passcode);
    }

    @Test
    void testGeneratePasscode_ReturnsCorrectLength() {
        int expectedLength = 6;
        String passcode = tokenGenerationService.generatePasscode();
        assertEquals(expectedLength, passcode.length());
    }

    @Test
    void testGeneratePasscode_ContainsOnlyAllowedCharacters() {
        String allowedChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        String passcode = tokenGenerationService.generatePasscode();
        assertTrue(passcode.chars().allMatch(c -> allowedChars.contains(String.valueOf((char) c))));
    }

    @Test
    void testGeneratePasscode_DoesntContainNotAllowedCharacters() {
        String notAllowedChars = "0OI1";
        String passcode = tokenGenerationService.generatePasscode();
        assertFalse(passcode.chars().anyMatch(c -> notAllowedChars.contains(String.valueOf((char) c))));
    }

    @Test
    void testGeneratePasscode_ReturnsUniqueValues() {
        String passcode1 = tokenGenerationService.generatePasscode();
        String passcode2 = tokenGenerationService.generatePasscode();
        assertNotEquals(passcode1, passcode2);
    }

    // ------------------------------------

}
