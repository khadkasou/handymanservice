package com.nepbrain.technology.auth.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

    AUTH001("User already exists"),
    AUTH002("User not found"),
    AUTH003("Invalid credentials"),
    AUTH004("Password change failed");
    private final String code;

    ErrorCodeEnum(String code) {
        this.code = code;
    }
}
