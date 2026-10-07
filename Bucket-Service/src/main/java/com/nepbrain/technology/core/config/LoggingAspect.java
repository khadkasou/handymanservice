package com.nepbrain.technology.core.config;

import com.nimbusds.jose.shaded.gson.Gson;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    private static final Gson gson = new Gson();

    @Before("execution(* com.nepbrain.technology.bucket.service.*.*(..)) && args(exchange,..)")
    public void logBeforeMethod(JoinPoint joinPoint, ServerWebExchange exchange) {
        log.info("Method Called: {}", joinPoint.getSignature().getName());
    }

    @AfterReturning(value = "execution(* com.nepbrain.technology.bucket.service.*.*(..))",
            returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {
        log.info("Method Executed: {}", joinPoint.getSignature().getName());
        
        // Handle non-serializable types that Gson can't serialize
        String resultString = serializeResult(result);
        log.info("Result: {}", resultString);
    }
    
    /**
     * Safely serializes the result object, handling non-serializable types.
     */
    private String serializeResult(Object result) {
        if (result == null) {
            return "null";
        }
        
        // Handle InputStream and other stream types
        if (result instanceof InputStream) {
            return String.format("InputStream[class=%s, available=%s]", 
                    result.getClass().getSimpleName(),
                    getAvailableBytes((InputStream) result));
        }
        
        // Handle other non-serializable types
        if (isNonSerializableType(result)) {
            return String.format("%s[class=%s, toString=%s]", 
                    result.getClass().getSimpleName(),
                    result.getClass().getName(),
                    result.toString());
        }
        
        // Try to serialize with Gson, catch any exceptions
        try {
            return gson.toJson(result);
        } catch (Exception e) {
            // If serialization fails, return a safe string representation
            return String.format("%s[class=%s, toString=%s]", 
                    result.getClass().getSimpleName(),
                    result.getClass().getName(),
                    result.toString());
        }
    }
    
    /**
     * Checks if the object is a type that typically can't be serialized by Gson.
     */
    private boolean isNonSerializableType(Object obj) {
        if (obj == null) {
            return false;
        }
        
        Class<?> clazz = obj.getClass();
        String className = clazz.getName();
        
        // Check for common non-serializable types
        return InputStream.class.isAssignableFrom(clazz) ||
               className.startsWith("java.io.") ||
               className.startsWith("java.nio.") ||
               className.startsWith("java.net.") ||
               className.startsWith("javax.servlet.") ||
               className.startsWith("org.springframework.web.");
    }
    
    /**
     * Gets available bytes from InputStream without consuming it.
     */
    private String getAvailableBytes(InputStream inputStream) {
        try {
            if (inputStream instanceof ByteArrayInputStream) {
                return String.valueOf(((ByteArrayInputStream) inputStream).available());
            }
            // For other InputStream types, try to get available bytes
            int available = inputStream.available();
            return available >= 0 ? String.valueOf(available) : "unknown";
        } catch (IOException e) {
            return "error: " + e.getMessage();
        }
    }
}
