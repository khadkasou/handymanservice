package com.nepbrain.technology.customer.enums;

import lombok.Getter;

@Getter
public enum AddressTypeEnum {
    PERMANENT("Permanent Address"),
    TEMPORARY("Temporary Address"),
    BILLING("Billing Address"),
    SHIPPING("Shipping Address"),
    OFFICE("Office Address");

    private final String description;

    AddressTypeEnum(String description) {
        this.description = description;
    }
}

