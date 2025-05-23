package com.mariuszilinskas.vsp.auth.identity.exception;

public class PasscodeValidationException extends RuntimeException {

    public PasscodeValidationException() {
        super("Incorrect passcode. Try again.");
    }

}
