package com.mariuszilinskas.vsp.authservice.dto;

public record ResetPasswordEmailRequest(
        String firstName,
        String email,
        String resetToken
) {}
