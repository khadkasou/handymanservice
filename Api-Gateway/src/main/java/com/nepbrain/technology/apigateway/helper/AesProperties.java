package com.nepbrain.technology.apigateway.helper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "aes")
public class AesProperties {

    private String algorithm;
    private String padding;
    private String secret;
}
