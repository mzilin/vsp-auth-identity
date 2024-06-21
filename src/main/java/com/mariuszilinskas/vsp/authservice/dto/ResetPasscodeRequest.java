package com.mariuszilinskas.vsp.authservice.dto;

import java.util.UUID;

public record ResetPasscodeRequest(
        UUID userId,
        String email
) {}
