package com.nepbrain.technology.apigateway.enums;

import lombok.Getter;

/**
 * @author suroj.awal
 * @created 2025-07-16
 */

@Getter
public enum ErrorCodeEnum {

    AES001("AES001", "Failed to encrypt data using AES."),
    AES002("AES002", "Failed to decrypt data using AES.");

    private final String code;
    private final String message;

    ErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }


}
