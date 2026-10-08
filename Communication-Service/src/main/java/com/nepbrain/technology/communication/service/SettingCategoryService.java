package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.payload.request.SettingCategoryListRequest;
import com.nepbrain.technology.communication.payload.request.SettingCategoryRequest;
import com.nepbrain.technology.core.payload.response.GlobalResponse;

import java.util.UUID;

public interface SettingCategoryService {

    GlobalResponse create(SettingCategoryRequest request);

    GlobalResponse update(UUID id, SettingCategoryRequest request);

    GlobalResponse getById(UUID id);

    GlobalResponse list(SettingCategoryListRequest request);

    GlobalResponse delete(UUID id);
}
