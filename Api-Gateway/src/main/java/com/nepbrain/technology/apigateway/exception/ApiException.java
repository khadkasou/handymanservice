package com.nepbrain.technology.apigateway.exception;

import com.nepbrain.technology.apigateway.helper.MessageBundle;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class ApiException extends RuntimeException {

    private String code;
    private String message;
    private HttpStatus status;

    public ApiException() {
        super();
    }

    public ApiException(String code) {
        this.code = code;
        this.message = MessageBundle.getErrorMessage(code);
        this.status = HttpStatus.UNPROCESSABLE_ENTITY;
    }

}
