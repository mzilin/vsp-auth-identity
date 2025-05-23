package com.mariuszilinskas.vsp.auth.identity.service;

import java.util.UUID;

public interface DataDeletionService {

    void deleteUserAuthData(UUID userId);

}
