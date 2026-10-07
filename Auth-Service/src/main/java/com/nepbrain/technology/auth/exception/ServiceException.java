package com.nepbrain.technology.auth.exception;

import com.nepbrain.technology.auth.util.MessageBundle;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ServiceException extends RuntimeException {

    private final boolean success;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public ServiceException(String code, HttpStatus httpStatus) {
        this.success = false;
        this.code = code;
        this.message = MessageBundle.getMessage(code);
        this.httpStatus = httpStatus;
    }

    public ServiceException(String code, String message, HttpStatus httpStatus) {
        this.success = false;
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
