package com.nepbrain.technology.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component("simpleClientAddressResolver")
public class SimpleClientAddressResolver implements KeyResolver {
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return Mono.just(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).toString());
    }
}
