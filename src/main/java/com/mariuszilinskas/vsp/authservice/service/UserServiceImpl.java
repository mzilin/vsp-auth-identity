package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.client.UserFeignClient;
import com.mariuszilinskas.vsp.authservice.dto.AuthDetails;
import com.mariuszilinskas.vsp.authservice.exception.FeignClientException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    public AuthDetails getUserAuthDetails(String email) {
        logger.info("Getting User ID for User [email: '{}']", email);
        try {
            return userFeignClient.getUserAuthDetails(email);
        } catch (FeignException ex) {
            throw new FeignClientException(email, ex);
        }
    }

}
