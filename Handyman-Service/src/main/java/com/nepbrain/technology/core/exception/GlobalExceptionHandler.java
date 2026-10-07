package com.nepbrain.technology.core.exception;

import com.nepbrain.technology.core.enums.ApiStatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import com.nepbrain.technology.core.util.MessageBundle;
import com.nimbusds.jose.shaded.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<GlobalResponse> handleRemitsException(GlobalException e) {
        logger.error("GlobalException caught: code={}, message={}, httpStatus={}", 
                e.getCode(), e.getMessage(), e.getHttpStatus());
        GlobalResponse response = GlobalResponseBuilder.buildFailResponse(e);
        return ResponseEntity.status(e.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalResponse> handleException(Exception e) {
        logger.error("Unhandled exception caught", e);
        GlobalResponse response = GlobalResponseBuilder.buildUnknownFailResponse(e);
        // Ensure message is not null
        if (response.getMessage() == null || response.getMessage().isEmpty()) {
            response.setMessage(e.getClass().getSimpleName() + ": " + 
                    (e.getMessage() != null ? e.getMessage() : "An unexpected error occurred"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<GlobalResponse> handleException(HandlerMethodValidationException e) {
        List<ParameterErrors> bindingResult = e.getBeanResults();
        Map<String, List<String>> data = bindingResult.stream()
                .flatMap(result -> result.getFieldErrors().stream())
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(DefaultMessageSourceResolvable::getDefaultMessage, Collectors.toList())
                ));
        GlobalResponse globalResponse = new GlobalResponse();
        globalResponse.setStatus(ApiStatusEnum.FAILED);
        globalResponse.setCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        globalResponse.setData(data);
        if (logger.isInfoEnabled())
            logger.info("METHOD ARGUMENT NOT VALID EXCEPTION: {}", new Gson().toJson(globalResponse));
        return ResponseEntity.badRequest()
                .body(globalResponse);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponse> handleException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String, List<String>> errors = bindingResult.getAllErrors().stream().collect(Collectors.groupingBy(
                error -> (error instanceof FieldError fieldError) ? fieldError.getField() : error.getObjectName(),
                Collectors.mapping(DefaultMessageSourceResolvable::getDefaultMessage, Collectors.toList())));
        GlobalResponse globalResponse = new GlobalResponse();
        globalResponse.setStatus(ApiStatusEnum.FAILED);
        globalResponse.setCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        globalResponse.setData(errors);
        return ResponseEntity.badRequest()
                .body(globalResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<GlobalResponse> handleResourceNotFoundException(NoResourceFoundException e) {
        logger.warn("Resource not found: {}", e.getResourcePath());
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(ApiStatusEnum.FAILED)
                .code(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .message("Resource not found: " + e.getResourcePath())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(globalResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<GlobalResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
        logger.warn("No handler found for {} {}", e.getHttpMethod(), e.getRequestURL());
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(ApiStatusEnum.FAILED)
                .code(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .message("No handler found for " + e.getHttpMethod() + " " + e.getRequestURL())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(globalResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<GlobalResponse> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logger.warn("HTTP method not supported: {}", e.getMethod());
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(ApiStatusEnum.FAILED)
                .code(String.valueOf(HttpStatus.METHOD_NOT_ALLOWED.value()))
                .message("HTTP method " + e.getMethod() + " is not supported for this endpoint. Supported methods: " + 
                        String.join(", ", e.getSupportedMethods()))
                .build();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(globalResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<GlobalResponse> handleAuthenticationException(final AuthenticationException e) {
        logger.warn("Authentication exception: {}", e.getMessage());
        String message = e.getMessage();
        if (message == null || message.isEmpty()) {
            message = "Unauthorized: Authentication required. Please provide a valid token.";
        }
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(ApiStatusEnum.FAILED)
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(globalResponse);
    }

    @ExceptionHandler(WebClientResponseException.Forbidden.class)
    public ResponseEntity<GlobalResponse> handleNotAuthorizedException(final WebClientResponseException.Forbidden e) {
        logger.warn("Forbidden exception: {}", e.getMessage());
        String message = e.getMessage();
        if (message == null || message.isEmpty()) {
            message = "Forbidden: You do not have permission to access this resource.";
        }
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(ApiStatusEnum.FAILED)
                .code(String.valueOf(HttpStatus.FORBIDDEN.value()))
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(globalResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GlobalResponse> handleAccessDeniedException(final AccessDeniedException e) {
        logger.warn("Access denied exception: {}", e.getMessage());
        String message = e.getMessage();
        if (message == null || message.isEmpty()) {
            // Try to get message from MessageBundle, fallback to default
            String bundleMessage = MessageBundle.getErrorMessage("PER004");
            message = (bundleMessage != null && !bundleMessage.isEmpty()) 
                    ? bundleMessage 
                    : "Forbidden: You do not have permission to access this resource.";
        }
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(ApiStatusEnum.FAILED)
                .code(String.valueOf(HttpStatus.FORBIDDEN.value()))
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(globalResponse);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<GlobalResponse> handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        GlobalResponse globalResponse = new GlobalResponse();
        globalResponse.setStatus(ApiStatusEnum.FAILED);
        globalResponse.setMessage(MessageBundle.getErrorMessage("HED001"));
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                .body(globalResponse);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(DataIntegrityViolationException ex, final WebRequest request) {
        final List<String> errors = new ArrayList<String>();

        if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException violation) {

            if (violation.getConstraintName() != null && violation.getConstraintName().contains("unique_")) {
                errors.add("You are trying to save duplicate value" + " "+violation.getConstraintName());
            } else {
                errors.add("Data integrity violation: " + violation.getMessage());
            }
        } else {
            errors.add("Data integrity violation: " + ex.getMessage());
        }
        GlobalResponse globalResponse = new GlobalResponse();
        globalResponse.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(globalResponse);
    }
}
