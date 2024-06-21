package com.mariuszilinskas.vsp.authservice.dto;

public record WelcomeEmailRequest(
        String firstName,
        String email
) {}
