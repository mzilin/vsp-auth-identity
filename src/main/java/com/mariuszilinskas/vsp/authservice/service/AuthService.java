package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.CredentialsRequest;
import com.mariuszilinskas.vsp.authservice.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    void createPasswordAndSetPasscode(CredentialsRequest request);

    void authenticateUser(LoginRequest request, HttpServletResponse response);

    void refreshTokens(HttpServletRequest request, HttpServletResponse response);

}
