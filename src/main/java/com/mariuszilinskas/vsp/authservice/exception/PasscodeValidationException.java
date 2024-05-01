package com.mariuszilinskas.vsp.authservice.exception;

public class PasscodeValidationException extends RuntimeException {

    public PasscodeValidationException() {
        super("Incorrect pin code. Try again.");
    }
}
