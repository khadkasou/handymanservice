package com.nepbrain.technology.booking.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

    BKG001("Booking not found"),
    BKG002("Coupon not found"),
    BKG003("Invalid booking status");
    private final String code;

    ErrorCodeEnum(String code) {
        this.code = code;
    }
}
