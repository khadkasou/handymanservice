package com.nepbrain.technology.apigateway.enums;


import lombok.Getter;

@Getter
public enum LogEnum {
    APPLICATION("Application"),
    ACTIVITY("Activity"),
    LOGIN("Login");

    public final String value;

    LogEnum(String value) {
        this.value = value;
    }
}
