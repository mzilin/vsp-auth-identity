package com.mariuszilinskas.vsp.authservice.exception;

public class ResetTokenValidationException extends RuntimeException {

    public ResetTokenValidationException() {
        super("Reset Token doesn't exist or has expired.");
    }

}
