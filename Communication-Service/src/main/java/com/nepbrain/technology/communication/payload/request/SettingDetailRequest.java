package com.nepbrain.technology.communication.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SettingDetailRequest {

    @NotBlank
    private String key;

    private String value;

    private String displayName;

    @NotNull
    private UUID settingCategoryId;
}
