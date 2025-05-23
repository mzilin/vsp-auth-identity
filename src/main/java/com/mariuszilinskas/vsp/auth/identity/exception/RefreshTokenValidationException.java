package com.mariuszilinskas.vsp.auth.identity.exception;

public class RefreshTokenValidationException extends RuntimeException {

    public RefreshTokenValidationException(String message) {
        super(message);
    }

}
