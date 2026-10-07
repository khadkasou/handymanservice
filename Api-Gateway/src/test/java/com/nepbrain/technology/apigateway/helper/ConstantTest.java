package com.nepbrain.technology.apigateway.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConstantTest {

    @Test
    void keyConstantsArePresentAndNonBlank() {
        assertNotNull(Constant.UNAUTHORIZED_ACCESS);
        assertFalse(Constant.UNAUTHORIZED_ACCESS.isBlank());

        assertEquals("X-XSRF-TOKEN", Constant.X_XSRF_TOKEN);
        assertEquals("XSRF Token Missing.", Constant.XSRF_TOKEN_MISSING);
        assertEquals("Invalid XSRF Token.", Constant.INVALID_XSRF_TOKEN);
        assertEquals("Invalid XSRF Token.", Constant.INVALID_XSRF_TOKEN_FORMAT);
        assertEquals("v2/api/", Constant.VALID_API_URL);
        assertEquals("Bearer ", Constant.BEARER_TOKEN_FORMAT);
        assertEquals("sub", Constant.JWT_SUBJECT);
        assertEquals("User-Agent", Constant.USER_AGENT);
        assertEquals("Device-Id", Constant.DEVICE_ID);
        assertEquals("Request limit exceeded", Constant.REQUEST_LIMIT_EXCEED);
        assertEquals("Blocked", Constant.BLOCKED);
        assertEquals("Request-Id", Constant.REQUEST_ID);
    }
}
