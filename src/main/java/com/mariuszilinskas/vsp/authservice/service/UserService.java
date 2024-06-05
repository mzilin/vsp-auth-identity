package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.dto.AuthDetails;

public interface UserService {

    AuthDetails getUserAuthDetails(String email);

}
