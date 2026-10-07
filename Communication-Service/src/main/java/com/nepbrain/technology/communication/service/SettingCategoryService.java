package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.payload.request.SettingCategoryListRequest;
import com.nepbrain.technology.communication.payload.request.SettingCategoryRequest;
import com.nepbrain.technology.communication.payload.response.DatapaginationResponse;
import com.nepbrain.technology.communication.payload.response.SettingCategoryResponse;

import java.util.UUID;

public interface SettingCategoryService {

    void create(SettingCategoryRequest request);

    void update(UUID id, SettingCategoryRequest request);

    SettingCategoryResponse getById(UUID id);

    DatapaginationResponse list(SettingCategoryListRequest request);

    void delete(UUID id);
}
