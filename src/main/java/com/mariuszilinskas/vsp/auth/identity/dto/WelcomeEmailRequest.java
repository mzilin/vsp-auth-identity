package com.mariuszilinskas.vsp.auth.identity.dto;

public record WelcomeEmailRequest(
        String type,
        String firstName,
        String email
) {}
