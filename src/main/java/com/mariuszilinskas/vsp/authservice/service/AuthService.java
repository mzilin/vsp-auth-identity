package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.CreateCredentialsRequest;

public interface AuthService {

    void createPasswordAndSetPasscode(CreateCredentialsRequest request);

}
