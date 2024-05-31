package com.mariuszilinskas.vsp.authservice.service;

import java.util.UUID;

public interface UserService {

    UUID getUserIdByEmail(String email);

}
