package com.nepbrain.technology.communication.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingRequest {

    @NotBlank
    private String settingCode;

    @NotBlank
    private String name;

    private String description;
}
