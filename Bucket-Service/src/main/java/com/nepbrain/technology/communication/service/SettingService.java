package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.payload.request.SettingListRequest;
import com.nepbrain.technology.communication.payload.request.SettingRequest;
import com.nepbrain.technology.communication.payload.response.DatapaginationResponse;
import com.nepbrain.technology.communication.payload.response.SettingResponse;

import java.util.UUID;

public interface SettingService {

    void create(SettingRequest request);

    void update(UUID id, SettingRequest request);

    SettingResponse getById(UUID id);

    DatapaginationResponse list(SettingListRequest request);

    void delete(UUID id);
}
