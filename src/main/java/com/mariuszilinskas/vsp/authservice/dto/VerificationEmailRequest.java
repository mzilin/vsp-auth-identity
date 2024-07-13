package com.mariuszilinskas.vsp.authservice.dto;

public record VerificationEmailRequest(
        String type,
        String firstName,
        String email,
        String passcode
) {}
