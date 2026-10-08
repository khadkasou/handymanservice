package com.nepbrain.technology.core.payload.response;

import com.nepbrain.technology.core.enums.ApiStatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.util.MessageBundle;

public class GlobalResponseBuilder {

    private GlobalResponseBuilder() {
    }

    public static GlobalResponse buildSuccessResponse(Object data) {
        return GlobalResponse.builder()
                .status(ApiStatusEnum.SUCCESS)
                .data(data)
                .build();
    }

    public static GlobalResponse buildSuccessResponse(String message) {
        return GlobalResponse.builder()
                .status(ApiStatusEnum.SUCCESS)
                .message(resolveSuccessMessage(message))
                .build();
    }

    public static GlobalResponse buildSuccessResponse(String message, Object data) {
        return GlobalResponse.builder()
                .status(ApiStatusEnum.SUCCESS)
                .message(resolveSuccessMessage(message))
                .data(data)
                .build();
    }

    private static String resolveSuccessMessage(String key) {
        if (key == null || key.isBlank()) {
            return key;
        }
        String resolved = MessageBundle.getSuccessMessage(key);
        if (resolved == null || resolved.isBlank()) {
            return key;
        }
        return resolved;
    }

    public static GlobalResponse buildSuccessResponseWithMessage(String code, String message) {
        return GlobalResponse.builder()
                .status(ApiStatusEnum.SUCCESS)
                .message(message)
                .code(code)
                .build();
    }

    public static GlobalResponse buildSuccessResponseWithData(String code, String message, Object object) {
        return GlobalResponse.builder()
                .status(ApiStatusEnum.SUCCESS)
                .code(code)
                .message(message)
                .data(object)
                .build();
    }

    public static GlobalResponse buildFailResponse(String code) {
        return GlobalResponse.builder()
                .status(ApiStatusEnum.FAILED)
                .message(MessageBundle.getErrorMessage(code))
                .code(code)
                .build();
    }

    public static GlobalResponse buildFailResponse(GlobalException e) {
        return GlobalResponse.builder()
                .status(ApiStatusEnum.FAILED)
                .message(e.getMessage())
                .code(e.getCode())
                .build();
    }

    public static GlobalResponse buildUnknownFailResponse(Exception e) {
        return GlobalResponse.builder()
                .status(ApiStatusEnum.FAILED)
                .message(e.getMessage())
                .build();
    }

    public static GlobalResponse buildPendingResponse(GlobalException e) {
        return GlobalResponse.builder()
                .status(ApiStatusEnum.PENDING)
                .message(e.getMessage())
                .build();
    }
}
