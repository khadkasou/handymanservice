package com.nepbrain.technology.auth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(@Nonnull HttpServletRequest request, HttpServletResponse response, @Nonnull AuthenticationException authException) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", HttpServletResponse.SC_UNAUTHORIZED);
        map.put("success", false);
        map.put("message", "Your session has expired or you’re not signed in. Please log in to continue.");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), map);
    }
}
