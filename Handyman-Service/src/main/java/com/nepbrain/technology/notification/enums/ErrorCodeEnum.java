package com.nepbrain.technology.notification.enums;
import lombok.Getter;
@Getter
public enum ErrorCodeEnum {
    NTF001("Notification not found");
    private final String code;
    ErrorCodeEnum(String code) { this.code = code; }
}
