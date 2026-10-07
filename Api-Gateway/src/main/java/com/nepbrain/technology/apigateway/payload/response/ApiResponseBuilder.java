package com.nepbrain.technology.apigateway.payload.response;

import com.nepbrain.technology.apigateway.exception.ExceptionUtil;
import com.nepbrain.technology.apigateway.helper.MessageBundle;
import java.text.MessageFormat;

public class ApiResponseBuilder {

    private ApiResponseBuilder() {
    }

    public static ServiceResponse buildSuccessResponse(String code) {
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setSuccess(Boolean.TRUE);
        serviceResponse.setMessage(MessageBundle.getMessage(code));
        return serviceResponse;
    }

    public static ServiceResponse buildSuccessResponse(String code, Object... args) {
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setSuccess(Boolean.TRUE);
        String rawMessage = MessageBundle.getErrorMessage(code);
        serviceResponse.setMessage(MessageFormat.format(rawMessage, args));
        return serviceResponse;
    }

    public static ServiceResponse buildSuccessResponse(String code, Object data) {
        ServiceResponse serviceResponse = buildSuccessResponse(code);
        serviceResponse.setData(data);
        return serviceResponse;
    }

    public static ApiErrorResponse buildErrorResponse(String code, Throwable throwable) {
        return ApiErrorResponse.builder()
                .success(Boolean.FALSE)
                .message(MessageBundle.getErrorMessage(code))
                .code(code)
                .debugMessage(ExceptionUtil.getStackTraceString(throwable))
                .build();
    }

    public static ApiErrorResponse buildException(String code, String message, Throwable throwable) {
        return ApiErrorResponse.builder()
                .success(Boolean.FALSE)
                .message(message)
                .code(code)
                .debugMessage(ExceptionUtil.getStackTraceString(throwable))
                .build();
    }


}
