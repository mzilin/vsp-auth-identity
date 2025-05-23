package com.mariuszilinskas.vsp.auth.identity.exception;

public class UserRetrievalException extends RuntimeException {

    public UserRetrievalException() {
        super("Failed to retrieve user information. Try again later.");
    }

}
