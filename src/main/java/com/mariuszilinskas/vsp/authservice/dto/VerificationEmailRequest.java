package com.mariuszilinskas.vsp.authservice.dto;

public record VerificationEmailRequest(
        String firstName,
        String email,
        String passcode
) {}
