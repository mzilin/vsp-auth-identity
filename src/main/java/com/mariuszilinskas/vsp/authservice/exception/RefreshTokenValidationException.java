package com.mariuszilinskas.vsp.authservice.exception;

public class RefreshTokenValidationException extends RuntimeException {

    public RefreshTokenValidationException(String message) {
        super(message);
    }
}
