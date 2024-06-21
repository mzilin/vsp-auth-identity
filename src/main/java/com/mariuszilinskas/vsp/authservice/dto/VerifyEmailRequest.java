package com.mariuszilinskas.vsp.authservice.dto;

public record VerifyEmailRequest(
        String firstName,
        String email,
        String passcode
) {}
