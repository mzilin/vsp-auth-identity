package com.mariuszilinskas.vsp.authservice.client;

import com.mariuszilinskas.vsp.authservice.enums.UserRole;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient("users")
public interface UserFeignClient {

    @PatchMapping(value = "/{userId}/verify", consumes = "application/json")
    ResponseEntity<Void> verifyUserEmail(@PathVariable("userId") UUID userId);

    @GetMapping(value = "/id-by-email", consumes = "application/json")
    UUID getUserIdByEmail(String email);

    @GetMapping(value = "/{userId}/role", consumes = "application/json")
    UserRole getUserRole(@PathVariable("userId") UUID userId);

}
