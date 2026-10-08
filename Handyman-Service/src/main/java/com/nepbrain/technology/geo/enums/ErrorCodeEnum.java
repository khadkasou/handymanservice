package com.nepbrain.technology.geo.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

    GEO001("Country not found"),
    GEO002("State not found"),
    GEO003("City not found");
    private final String code;

    ErrorCodeEnum(String code) {
        this.code = code;
    }
}
