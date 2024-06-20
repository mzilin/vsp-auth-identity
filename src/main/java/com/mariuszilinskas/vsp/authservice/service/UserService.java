package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.AuthDetails;

import java.util.UUID;

public interface UserService {

    AuthDetails getUserAuthDetailsWithEmail(String email);

    AuthDetails getUserAuthDetailsWithId(UUID userId);

}
