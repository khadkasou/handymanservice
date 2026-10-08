package com.nepbrain.technology.config.payload.response;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfigurationResponse {

    private Map<String, String> configurations;
}
