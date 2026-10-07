package com.nepbrain.technology.apigateway.config.security;

import lombok.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCorsFilter implements WebFilter {

    private final CorsWebFilter corsWebFilter;

    public SimpleCorsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:4200",
                "http://*.localhost:4200",
                "http://localhost:8090",
                "https://*.nepbrain.technology",
                "https://*.axcessfoundation.com"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(Arrays.asList(
                "Request-Id",
                "X-XSRF-TOKEN",
                "Access-Control-Allow-Headers",
                "Origin",
                "Accept",
                "X-Requested-With",
                "Content-Type",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "Access-Control-Allow-Origin"
        ));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        this.corsWebFilter = new CorsWebFilter(exchange -> config);
    }

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        if (CorsUtils.isCorsRequest(exchange.getRequest())) {
            return corsWebFilter.filter(exchange, chain);
        }
        return chain.filter(exchange);
    }
}
