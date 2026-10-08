package com.nepbrain.technology.core.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.core.enums.ApiStatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        GlobalResponse errorResponse = createErrorResponse(request, accessDeniedException);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    private GlobalResponse createErrorResponse(
            HttpServletRequest request, AccessDeniedException accessDeniedException) {
        String message = accessDeniedException.getMessage();
        if (message == null || message.isEmpty()) {
            message = "Forbidden: You do not have permission to access this resource.";
        }

        return GlobalResponse.builder()
                .status(ApiStatusEnum.FAILED)
                .code(String.valueOf(HttpStatus.FORBIDDEN.value()))
                .message(message)
                .build();
    }
}
