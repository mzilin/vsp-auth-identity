package com.mariuszilinskas.vsp.authservice.exception;

public class CredentialsValidationException extends RuntimeException {

    public CredentialsValidationException() {
        super("Your email or password is incorrect");
    }

}
