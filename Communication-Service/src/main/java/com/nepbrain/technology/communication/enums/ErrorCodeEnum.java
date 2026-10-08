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
    OTP001("Otp cannot be empty."),
    OTP002("Otp length should be 4 digits."),
    OTP004("Invalid otp."),
    MFA001("Multifactor authentication verification failed. As code is incorrect."),
    QR001("Unable to generate QR Code."),
    SMS001("SMS Route not found."),
    EMAIL001("Unable to send email."),
    DAT001("Invalid date filter."),
    ERR001("Invalid request payload."),

    ;

    private final String value;
    ErrorCodeEnum(String value) {
        this.value = value;
    }
}
