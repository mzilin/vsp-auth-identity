package com.mariuszilinskas.vsp.authservice.exception;

public class EmailVerificationException extends RuntimeException {

    public EmailVerificationException() {
        super("Email verification failed. Try again later.");
    }

}
