package com.nepbrain.technology.core.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.core.enums.ApiStatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        GlobalResponse errorResponse = createErrorResponse(request, authException);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    private GlobalResponse createErrorResponse(HttpServletRequest request, AuthenticationException authException) {
        String message = authException.getMessage();
        if (message == null || message.isEmpty()) {
            message = "Unauthorized: Authentication required. Please provide a valid token.";
        }

        return GlobalResponse.builder()
                .status(ApiStatusEnum.FAILED)
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .message(message)
                .build();
    }
}
