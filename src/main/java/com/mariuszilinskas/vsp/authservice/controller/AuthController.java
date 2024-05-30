package com.mariuszilinskas.vsp.authservice.controller;

import com.mariuszilinskas.vsp.authservice.dto.CredentialsRequest;
import com.mariuszilinskas.vsp.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This class provides REST APIs for handling CRUD operations related to authentication.
 *
 * @author Marius Zilinskas
 */
@RestController
@RequestMapping()
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/credentials")
    public ResponseEntity<Void> createPasswordAndSetPasscode(
            @Valid @RequestBody CredentialsRequest request
    ) {
        authService.createPasswordAndSetPasscode(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> loginUser(
    ) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/token")
    public ResponseEntity<Void> refreshTokens(
    ) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
