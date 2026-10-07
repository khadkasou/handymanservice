package com.nepbrain.technology.customer.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
    ERR001("Code"),
    ERR002("Code"),
    IAM004(""),
    IAM005("as"),
    SER001("as"),
    // Customer Errors
    CUS001("Customer code already exists"),
    CUS002("Customer not found"),
    CUS003("Customer email already exists"),
    // Customer Detail Errors
    CUSD001("Customer detail not found"),
    CUSD002("Customer detail already exists for this customer"),
    // Customer Address Errors
    CUSA001("Customer address not found"),
    CUSA002("Primary address not found"),
    // Customer Bank Detail Errors
    CUSB001("Customer bank detail not found"),
    CUSB002("Primary bank detail not found"),
    // Customer Preference Errors
    CUSP001("Customer preference not found"),
    CUSP002("Preference key already exists for this customer")
    ;
    private String code;
    ErrorCodeEnum(String code) {
        this.code = code;
    }
}
