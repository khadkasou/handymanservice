package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.payload.request.SettingDetailBulkUpdateRequest;
import com.nepbrain.technology.communication.payload.request.SettingDetailListRequest;
import com.nepbrain.technology.communication.payload.request.SettingDetailRequest;
import com.nepbrain.technology.communication.payload.response.DatapaginationResponse;
import com.nepbrain.technology.communication.payload.response.SettingDetailResponse;

import java.util.UUID;

public interface SettingDetailService {

    void create(SettingDetailRequest request);

    void update(UUID id, SettingDetailRequest request);

    SettingDetailResponse getById(UUID id);

    SettingDetailResponse getByKey(String key);

    DatapaginationResponse list(SettingDetailListRequest request);

    void delete(UUID id);

    void bulkUpdateBySettingCategoryId(SettingDetailBulkUpdateRequest request);
}
