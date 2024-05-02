package com.mariuszilinskas.vsp.authservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient("users")
public interface UserFeignClient {

    @PatchMapping(value = "/{userId}/email/verify", consumes = "application/json")
    void verifyUserEmail(@PathVariable("userId") UUID userId);

}
