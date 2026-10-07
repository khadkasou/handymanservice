package com.nepbrain.technology.apigateway.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RateLimitPropertiesTest {

    @Test
    void defaultsAreZero() {
        RateLimitProperties props = new RateLimitProperties();
        assertEquals(0L, props.getCapacity());
        assertEquals(0L, props.getRefillPeriod());
        assertEquals(0L, props.getBlockDuration());
    }

    @Test
    void settersAndGettersWork() {
        RateLimitProperties props = new RateLimitProperties();
        props.setCapacity(100L);
        props.setRefillPeriod(5L);
        props.setBlockDuration(15L);

        assertEquals(100L, props.getCapacity());
        assertEquals(5L, props.getRefillPeriod());
        assertEquals(15L, props.getBlockDuration());
    }
}
