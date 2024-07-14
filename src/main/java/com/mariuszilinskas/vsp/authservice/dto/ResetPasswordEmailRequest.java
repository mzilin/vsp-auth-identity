package com.mariuszilinskas.vsp.authservice.dto;

public record ResetPasswordEmailRequest(
        String type,
        String firstName,
        String email,
        String resetToken
) {}
