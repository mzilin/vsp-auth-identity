package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.*;

import java.util.UUID;

public interface PasswordService {

    void createNewPassword(CredentialsRequest request);

    void verifyPassword(VerifyPasswordRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void updatePassword(UUID userId, UpdatePasswordRequest request);

    void deleteUserPasswords(UUID userId);

}
