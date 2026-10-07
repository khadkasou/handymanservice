package com.nepbrain.technology.apigateway.helper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    private long capacity;
    private long refillPeriod;
    private long blockDuration;
}
