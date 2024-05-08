package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.ForgotPasswordRequest;
import com.mariuszilinskas.vsp.authservice.dto.ResetPasswordRequest;

import java.util.UUID;

public interface PasswordService {

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void deleteUserPasswords(UUID userId);

}
