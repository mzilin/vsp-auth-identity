package com.mariuszilinskas.vsp.authservice.exception;

public class PasswordValidationException extends RuntimeException {

    public PasswordValidationException() {
        super("Current password is incorrect");
    }
}
