package com.mariuszilinskas.vsp.authservice.dto;

import com.mariuszilinskas.vsp.authservice.enums.UserAuthority;
import com.mariuszilinskas.vsp.authservice.enums.UserRole;
import com.mariuszilinskas.vsp.authservice.enums.UserStatus;

import java.util.List;
import java.util.UUID;

public record AuthDetails(
        UUID userId,
        List<UserRole> roles,
        List<UserAuthority> authorities,
        UserStatus status
) {}
