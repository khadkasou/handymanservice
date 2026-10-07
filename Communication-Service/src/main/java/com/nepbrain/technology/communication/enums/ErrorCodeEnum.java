package com.nepbrain.technology.communication.enums;

/*
 * @Created At 24/03/2025
 * @Author suroj.awal
 */

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
    CUS001("Customer not found."),
    MSG001("Message already exists for this topic and language."),
    MSG002("Message not exist."),
    MSG003("Message detail not exist."),
    SUP001("Support email not found."),
    SET001("Setting not found."),
    SET002("Setting category not found."),
    SET003("Setting detail not found."),
    SRV001("User service is temporarily unavailable. Please ensure User-Service is running and try again."),

    ;

    private final String value;
    ErrorCodeEnum(String value) {
        this.value = value;
    }
}
