package com.mariuszilinskas.vsp.auth.identity.exception;

public class CredentialsValidationException extends RuntimeException {

    public CredentialsValidationException() {
        super("Your email or password is incorrect");
    }

}
