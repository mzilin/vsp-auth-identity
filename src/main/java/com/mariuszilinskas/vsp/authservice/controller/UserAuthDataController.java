package com.mariuszilinskas.vsp.authservice.controller;

import com.mariuszilinskas.vsp.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This class provides REST APIs for handling CRUD operations related to user data in auth service.
 *
 * @author Marius Zilinskas
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserAuthDataController {

    private final AuthService authService;

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserAuthData(){
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
