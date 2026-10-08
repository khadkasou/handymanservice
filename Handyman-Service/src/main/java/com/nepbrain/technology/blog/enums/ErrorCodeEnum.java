package com.nepbrain.technology.blog.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

    BLG001("Blog not found");
    private final String code;

    ErrorCodeEnum(String code) {
        this.code = code;
    }
}
