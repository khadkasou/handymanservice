package com.nepbrain.technology.review.enums;
import lombok.Getter;
@Getter
public enum ErrorCodeEnum {
    REV001("Booking rating not found");
    private final String code;
    ErrorCodeEnum(String code) { this.code = code; }
}
