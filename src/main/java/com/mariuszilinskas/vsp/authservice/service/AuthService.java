package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.CredentialsRequest;

public interface AuthService {

    void createPasswordAndSetPasscode(CredentialsRequest request);

}
