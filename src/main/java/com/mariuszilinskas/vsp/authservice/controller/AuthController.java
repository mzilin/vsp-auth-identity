package com.mariuszilinskas.vsp.authservice.controller;

import com.mariuszilinskas.vsp.authservice.dto.CredentialsRequest;
import com.mariuszilinskas.vsp.authservice.dto.LoginRequest;
import com.mariuszilinskas.vsp.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
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
    public ResponseEntity<Void> authenticateUser(
            @Valid @RequestBody LoginRequest request,
            @NonNull HttpServletResponse response
    ) {
        authService.authenticateUser(request, response);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/token")
    public ResponseEntity<Void> refreshTokens(
    ) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
