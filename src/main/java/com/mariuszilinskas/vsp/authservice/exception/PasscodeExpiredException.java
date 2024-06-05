package com.mariuszilinskas.vsp.authservice.exception;

public class PasscodeExpiredException extends RuntimeException {

    public PasscodeExpiredException() {
        super("This passcode has expired.");
    }
}
