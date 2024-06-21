package com.mariuszilinskas.vsp.authservice.client;

import com.mariuszilinskas.vsp.authservice.dto.AuthDetails;
import com.mariuszilinskas.vsp.authservice.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient("users")
public interface UserFeignClient {

    @PatchMapping(value = "/user/{userId}/verify", consumes = "application/json")
    ResponseEntity<Void> verifyUserEmail(@PathVariable("userId") UUID userId);

    @GetMapping(value = "/user/auth-details/by-email", consumes = "application/json")
    AuthDetails getUserAuthDetailsByEmail(@RequestParam String email);

    @GetMapping(value = "/user/auth-details/by-userid", consumes = "application/json")
    AuthDetails getUserAuthDetailsByUserId(@RequestParam UUID userId);

    @GetMapping(value = "/user/{userId}", consumes = "application/json")
    UserResponse getUser(@PathVariable("userId") UUID userId);

}
