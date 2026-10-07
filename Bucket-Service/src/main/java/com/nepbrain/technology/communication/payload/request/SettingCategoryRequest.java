package com.nepbrain.technology.communication.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SettingCategoryRequest {

    @NotBlank
    private String categoryCode;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private UUID settingId;
}
