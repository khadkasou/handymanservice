package com.nepbrain.technology.communication.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingDetailItemRequest {

    @NotBlank
    private String key;

    private String value;

    private String displayName;
}
