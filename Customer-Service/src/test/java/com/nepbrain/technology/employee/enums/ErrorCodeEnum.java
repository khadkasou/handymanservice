package com.nepbrain.technology.employee.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
    ERR001("Code"),
    ERR002("Code"),
    IAM004(""),
    IAM005("as"),
    SER001("as")
    ;
    private String code;
    ErrorCodeEnum(String code) {
        this.code = code;
    }
}
