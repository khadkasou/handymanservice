package com.nepbrain.technology.review.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

    REV001("Booking rating not found"),
    REV002("Booking rating status must be ACTIVE or INACTIVE");
    private final String code;

    ErrorCodeEnum(String code) {
        this.code = code;
    }
}
