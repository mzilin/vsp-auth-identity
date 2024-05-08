package com.mariuszilinskas.vsp.authservice.controller;

import com.mariuszilinskas.vsp.authservice.dto.VerifyPasscodeRequest;
import com.mariuszilinskas.vsp.authservice.service.PasscodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * This class provides REST APIs for handling CRUD operations related to user passcodes.
 *
 * @author Marius Zilinskas
 */
@RestController
@RequestMapping("/auth/passcode")
@RequiredArgsConstructor
public class PasscodeController {

    private final PasscodeService passcodeService;

    @PutMapping("/{userId}/verify-passcode")
    public ResponseEntity<Void> verifyPasscode(
            @PathVariable UUID userId,
            @Valid @RequestBody VerifyPasscodeRequest request
    ) {
        passcodeService.verifyPasscode(userId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{userId}/reset-passcode")
    public ResponseEntity<Void> resetPasscode(
            @PathVariable UUID userId
    ) {
        passcodeService.resetPasscode(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
