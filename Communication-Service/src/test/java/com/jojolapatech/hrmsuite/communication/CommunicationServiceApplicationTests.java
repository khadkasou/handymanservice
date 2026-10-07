package com.nepbrain.technology.hrmsuite.communication;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MockBean
class ApiGatewayApplicationTests {

    @Test
    void testAddition() {
        int i = 1;
        int j = i + 1;
        assertEquals(2, j, "The addition result should be 2");
    }

}
