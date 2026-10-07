package com.nepbrain.technology.communication.payload.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SettingDetailBulkUpdateRequest {

    @NotNull
    private UUID settingCategoryId;

    @Valid
    @NotEmpty
    private List<SettingDetailItemRequest> details;
}
