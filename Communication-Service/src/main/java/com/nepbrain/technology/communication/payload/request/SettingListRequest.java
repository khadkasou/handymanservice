package com.nepbrain.technology.communication.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingListRequest extends PaginationRequest {

    private String searchKey;
}
