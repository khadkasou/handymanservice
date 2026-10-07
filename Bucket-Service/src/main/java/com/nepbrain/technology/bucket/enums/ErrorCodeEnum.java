package com.nepbrain.technology.bucket.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

    IAM001("IAM001", "Account has been disabled."),
    IAM02("IAM002", "Client doesn't exist"),
    IAM03("IAM003", "Refresh token doesn't exist"),
    IAM04("IAM004", "Account has been expired."),
    IAM05("IAM005", "Credentials has been expired."),
    IAM06("IAM006", "Account has been locked."),
    ERR001("IAM007", "Unauthorized access! invalid username or password."),
    SER001("SER001", "Unauthorized access! invalid username or password.")
    ;

    public  final String code;
    public final String description;
    ErrorCodeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
