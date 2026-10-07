package com.nepbrain.technology.apigateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.apigateway.payload.response.ApiErrorResponse;
import com.nepbrain.technology.apigateway.helper.Constant;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {


    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder().code("IAM006").message(Constant.UNAUTHORIZED_ACCESS).build();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            byte[] errorResponse = objectMapper.writeValueAsBytes(apiErrorResponse);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(errorResponse)));
        } catch (JsonProcessingException jsonProcessingException) {
            return Mono.error(jsonProcessingException);
        }
    }
}
