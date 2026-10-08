package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.payload.request.SettingDetailBulkUpdateRequest;
import com.nepbrain.technology.communication.payload.request.SettingDetailListRequest;
import com.nepbrain.technology.communication.payload.request.SettingDetailRequest;
import com.nepbrain.technology.core.payload.response.GlobalResponse;

import java.util.UUID;

public interface SettingDetailService {

    GlobalResponse create(SettingDetailRequest request);

    GlobalResponse update(UUID id, SettingDetailRequest request);

    GlobalResponse getById(UUID id);

    GlobalResponse getByKey(String key);

    GlobalResponse list(SettingDetailListRequest request);

    GlobalResponse delete(UUID id);

    GlobalResponse bulkUpdateBySettingCategoryId(SettingDetailBulkUpdateRequest request);
}
