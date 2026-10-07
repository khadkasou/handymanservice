package com.nepbrain.technology.communication.enums;

import lombok.Getter;

@Getter
public enum SettingEnum {
    GENERAL("GENERAL", "General Setting"),
    EMAIL("EMAIL", "Email Setting"),
    SMS("SMS", "Sms Setting"),
    NOTIFICATION("NOTIFICATION", "Notification Setting");
    private final String code;
    private final String description;
    SettingEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
}