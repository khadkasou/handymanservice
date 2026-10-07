package com.nepbrain.technology.communication.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SettingDetailListRequest extends PaginationRequest {

    private UUID settingCategoryId;
    private String searchKey;
}
