package com.nepbrain.technology.helpdesk.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

    HLP001("Helpdesk ticket not found");
    private final String code;

    ErrorCodeEnum(String code) {
        this.code = code;
    }
}
