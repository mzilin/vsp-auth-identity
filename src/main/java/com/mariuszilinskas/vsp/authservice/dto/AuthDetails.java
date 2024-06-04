package com.mariuszilinskas.vsp.authservice.dto;

import java.util.List;
import java.util.UUID;

public record AuthDetails(
        UUID userId,
        List<String> roles,
        List<String> authorities
) {}
