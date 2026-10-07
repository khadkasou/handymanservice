package com.nepbrain.technology.communication.enums;

import lombok.Getter;

@Getter
public enum SettingCategoryEnum {
    GENERAL_COMPANY("COMPANY", "General Company", "GENERAL"),
    GENERAL_OTP("OTP", "General Default", "GENERAL"),

    EMAIL_AWS("AWS", "Aws Email Setting", "EMAIL"),
    EMAIL_MAILGUN("MAILGUN", "Mailgun Email Setting", "EMAIL"),
    EMAIL_SUPPORT("SUPPORT", "Support Email Setting", "EMAIL"),
    EMAIL_DEFAULT("DEFAULT_EMAIL", "Default Email Setting", "EMAIL"),

    SMS_AAKASH("AAKASH", "Aakash Sms Setting", "SMS"),
    SMS_SPARROW("SPARROW", "Sparrow Sms Setting", "SMS"),
    SMS_TWILIO("TWILIO", "Twilio Sms Setting", "SMS"),
    SMS_DEFAULT("DEFAULT_SMS", "Default Sms Setting", "SMS"),

    NOTIFICATION_PUSH("PUSH_NOTIFICATION", "Push Notification Setting", "NOTIFICATION"),
    NOTIFICATION_DEFAULT("DEFAULT_NOTIFICATION", "Default Notification Setting", "NOTIFICATION");

    private final String code;
    private final String description;
    private final String settingCode;

    SettingCategoryEnum(String code, String description, String settingCode) {
        this.code = code;
        this.description = description;
        this.settingCode = settingCode;
    }
}
