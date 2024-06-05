package com.mariuszilinskas.vsp.authservice.service;

import java.util.UUID;

public interface DataDeletionService {

    void deleteUserAuthData(UUID userId);

}
