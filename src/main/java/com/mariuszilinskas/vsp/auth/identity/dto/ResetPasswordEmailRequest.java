package com.mariuszilinskas.vsp.auth.identity.dto;

public record ResetPasswordEmailRequest(
        String type,
        String firstName,
        String email,
        String resetToken
) {}
