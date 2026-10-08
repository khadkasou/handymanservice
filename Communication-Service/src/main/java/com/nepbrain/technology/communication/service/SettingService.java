package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.payload.request.SettingListRequest;
import com.nepbrain.technology.communication.payload.request.SettingRequest;
import com.nepbrain.technology.core.payload.response.GlobalResponse;

import java.util.UUID;

public interface SettingService {

    GlobalResponse create(SettingRequest request);

    GlobalResponse update(UUID id, SettingRequest request);

    GlobalResponse getById(UUID id);

    GlobalResponse list(SettingListRequest request);

    GlobalResponse delete(UUID id);
}
