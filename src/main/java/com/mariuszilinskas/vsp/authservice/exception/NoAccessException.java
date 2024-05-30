package com.mariuszilinskas.vsp.authservice.exception;

public class NoAccessException extends RuntimeException {

    public NoAccessException() {
        super("This request is forbidden");
    }
}
