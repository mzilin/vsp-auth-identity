package com.mariuszilinskas.vsp.authservice.exception;

public class UserRetrievalException extends RuntimeException {

    public UserRetrievalException() {
        super("Failed to retrieve user information. Try again later.");
    }

}
