package com.nepbrain.technology.communication.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SettingCategoryListRequest extends PaginationRequest {

    private UUID settingId;
    private String searchKey;
}
