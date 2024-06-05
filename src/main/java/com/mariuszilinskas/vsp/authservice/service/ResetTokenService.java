package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.model.ResetToken;

import java.util.UUID;

public interface ResetTokenService {

    String createResetToken(UUID userId);

    ResetToken findResetToken(String token);

    void deleteUserResetTokens(UUID userid);

    void deleteExpiredResetTokens();

}
