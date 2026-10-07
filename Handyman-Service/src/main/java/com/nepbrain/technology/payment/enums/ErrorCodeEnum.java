package com.nepbrain.technology.payment.enums;
import lombok.Getter;
@Getter
public enum ErrorCodeEnum {
    PAY001("Payment not found"),
    PAY002("Wallet not found"),
    PAY003("Bank not found"),
    PAY004("Gateway not found");
    private final String code;
    ErrorCodeEnum(String code) { this.code = code; }
}
