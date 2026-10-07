package com.nepbrain.technology.core.exception;

import com.nepbrain.technology.core.util.MessageBundle;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class GlobalException extends RuntimeException {

    private final String code;
    private final HttpStatus httpStatus;
    private final String debugMessage;

    public GlobalException(String code) {
        super(MessageBundle.getErrorMessage(code));
        this.code = code;
        this.httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        this.debugMessage = "";
    }

    public GlobalException(String code, List<String> param) {
        super(String.format(MessageBundle.getErrorMessage(code), String.join(",", param)));
        this.code = code;
        this.httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        this.debugMessage = "";
    }

    public GlobalException(String code, String message) {
        super(message);
        this.code = code;
        this.httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        this.debugMessage = "";
    }

    public GlobalException(String code, Throwable throwable) {
        super(throwable.getMessage());
        this.code = code;
        this.httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        this.debugMessage = ExceptionUtil.getStackTraceString(throwable);
    }

    public GlobalException(String code, HttpStatus httpStatus) {
        super(MessageBundle.getErrorMessage(code));
        this.code = code;
        this.httpStatus = httpStatus;
        this.debugMessage = "";
    }

    public GlobalException(String code, HttpStatus httpStatus, String debugMessage) {
        super(MessageBundle.getErrorMessage(code));
        this.code = code;
        this.httpStatus = httpStatus;
        this.debugMessage = debugMessage;
    }

    public GlobalException(String code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
        this.debugMessage = "";
    }
}
