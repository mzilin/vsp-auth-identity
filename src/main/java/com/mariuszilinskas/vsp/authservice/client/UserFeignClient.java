package com.mariuszilinskas.vsp.authservice.client;

import com.mariuszilinskas.vsp.authservice.dto.AuthDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient("users")
public interface UserFeignClient {

    @PatchMapping(value = "/{userId}/verify", consumes = "application/json")
    ResponseEntity<Void> verifyUserEmail(@PathVariable("userId") UUID userId);

    @GetMapping(value = "/auth/details/email", consumes = "application/json")
    AuthDetails getUserAuthDetailsWithEmail(@RequestParam String email);

    @GetMapping(value = "/auth/details/id", consumes = "application/json")
    AuthDetails getUserAuthDetailsWithId(@RequestParam UUID userId);

}
