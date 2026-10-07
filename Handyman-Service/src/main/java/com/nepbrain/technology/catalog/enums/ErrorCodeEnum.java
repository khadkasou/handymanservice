package com.nepbrain.technology.catalog.enums;
import lombok.Getter;
@Getter
public enum ErrorCodeEnum {
    CAT001("Category not found"),
    SER001("Service not found"),
    FAV001("Favourite not found");
    private final String code;
    ErrorCodeEnum(String code) { this.code = code; }
}
