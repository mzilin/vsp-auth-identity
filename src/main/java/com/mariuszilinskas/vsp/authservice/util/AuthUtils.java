package com.mariuszilinskas.vsp.authservice.util;

public abstract class AuthUtils {

    private AuthUtils() {
        // Private constructor to prevent instantiation
    }

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd hh:mm:ss";

    public static final long FIFTEEN_MINS_IN_MILLIS = 15 * 60 * 1000; // 15 minutes

}
