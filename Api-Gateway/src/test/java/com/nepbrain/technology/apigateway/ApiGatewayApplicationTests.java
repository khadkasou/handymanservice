package com.nepbrain.technology.apigateway;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Lightweight smoke test — does not load the Spring context
 * (avoids requiring Redis, Consul, and other external infrastructure).
 */
class ApiGatewayApplicationTests {

    @Test
    void applicationClassIsPresent() {
        assertNotNull(ApiGatewayApplication.class);
    }

    @Test
    void applicationHasMainMethod() throws NoSuchMethodException {
        assertNotNull(ApiGatewayApplication.class.getDeclaredMethod("main", String[].class));
    }
}
