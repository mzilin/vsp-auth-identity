package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.client.UserFeignClient;
import com.mariuszilinskas.vsp.authservice.dto.AuthDetails;
import com.mariuszilinskas.vsp.authservice.exception.FeignClientException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Implementation of UserService interface.
 *
 * @author Marius Zilinskas
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserFeignClient userFeignClient;

    @Override
    public AuthDetails getUserAuthDetailsWithEmail(String email) {
        return getUserAuthDetails(() -> userFeignClient.getUserAuthDetailsByEmail(email), email);
    }

    @Override
    public AuthDetails getUserAuthDetailsWithId(UUID userId) {
        return getUserAuthDetails(() -> userFeignClient.getUserAuthDetailsByUserId(userId), userId.toString());
    }

    private AuthDetails getUserAuthDetails(Supplier<AuthDetails> supplier, String identifier) {
        logger.info("Getting User Auth Details for User [identifier: '{}']", identifier);
        try {
            return supplier.get();
        } catch (FeignException ex) {
            throw new FeignClientException(identifier, ex);
        }
    }

}
