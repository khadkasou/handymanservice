package com.nepbrain.technology.config.payload.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponse {

    private List<Map<String, Object>> category;
    private List<Map<String, Object>> service;
    private List<Map<String, Object>> provider;
    private List<Map<String, Object>> featuredServices;
    private Map<String, Object> configurations;
}
