package com.nepbrain.technology.apigateway.helper;

import org.springframework.stereotype.Component;

@Component
public class Constant {
    private Constant(){

    }

    public static final String UNAUTHORIZED_ACCESS = "You're not logged in please login to proceed!";
    public static final String X_XSRF_TOKEN = "X-XSRF-TOKEN";
    public static final String XSRF_TOKEN_MISSING = "XSRF Token Missing.";
    public static final String INVALID_XSRF_TOKEN = "Invalid XSRF Token.";
    public static final String INVALID_XSRF_TOKEN_FORMAT = "Invalid XSRF Token.";
    public static final String VALID_API_URL = "v2/api/";
    public static final String BEARER_TOKEN_FORMAT = "Bearer ";
    public static final String JWT_SUBJECT = "sub";
    public static final String USER_AGENT = "User-Agent";
    public static final String DEVICE_ID = "Device-Id";
    public static final String REQUEST_LIMIT_EXCEED = "Request limit exceeded";
    public static final String BLOCKED = "Blocked";
    public static final String REQUEST_ID = "Request-Id";

}
