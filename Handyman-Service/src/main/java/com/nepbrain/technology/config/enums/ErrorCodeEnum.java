package com.nepbrain.technology.config.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

    CFG001("Configuration not found");
    private final String code;

    ErrorCodeEnum(String code) {
        this.code = code;
    }
}
