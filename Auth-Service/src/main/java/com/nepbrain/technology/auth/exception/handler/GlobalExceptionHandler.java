package com.nepbrain.technology.auth.exception.handler;

import com.nepbrain.technology.auth.exception.ServiceException;
import com.nepbrain.technology.auth.payload.response.ServiceResponse;
import com.nepbrain.technology.auth.payload.response.ServiceResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles generic exceptions.
     *
     * @param e the exception
     * @return a ResponseEntity containing a ServiceResponse with the error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServiceResponse> handleException(Exception e) {
        logger.error("Exception {}", e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(ServiceResponseBuilder.buildException(e, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * Handles custom exceptions.
     *
     * @param e the custom exception
     * @return a ResponseEntity containing a ServiceResponse with the error details
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ServiceResponse> handleCustomException(ServiceException e) {
        ServiceResponse response = new ServiceResponse();
        response.setCode(e.getCode());
        response.setMessage(e.getMessage());
        logger.error("Custom exception {}", e.getMessage(), e);
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    /**
     * Handles NoResourceFoundException.
     *
     * @param e the NoResourceFoundException
     * @return a ResponseEntity with a 404 Not Found status
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ServiceResponse> handleNoResourceFoundException(
            NoResourceFoundException e) {
        logger.error("NoResourceFoundException : {}", e.getMessage(), e);
        return ResponseEntity.notFound().build();
    }

    /**
     * Handles MethodArgumentNotValidException.
     *
     * @param e the MethodArgumentNotValidException
     * @return a ResponseEntity containing a ServiceResponse with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ServiceResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errors =
                bindingResult.getAllErrors().stream()
                        .collect(
                                Collectors.groupingBy(
                                        error -> {
                                            if (error instanceof FieldError fieldError) {
                                                return fieldError.getField();
                                            } else {
                                                return error.getObjectName();
                                            }
                                        },
                                        Collectors.collectingAndThen(
                                                Collectors.mapping(
                                                        DefaultMessageSourceResolvable::getDefaultMessage, Collectors.toList()),
                                                List::getFirst)));
        ServiceResponse response = new ServiceResponse();
        response.setSuccess(false);
        response.setMessage("Validation error");
        response.setData(errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ServiceResponse> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ServiceResponseBuilder.buildException(String.valueOf(HttpStatus.FORBIDDEN.value()), e.getMessage()));
    }

}
