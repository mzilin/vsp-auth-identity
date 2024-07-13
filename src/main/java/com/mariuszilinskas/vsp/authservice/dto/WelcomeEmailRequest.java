package com.mariuszilinskas.vsp.authservice.dto;

public record WelcomeEmailRequest(
        String type,
        String firstName,
        String email
) {}
