package com.nepbrain.technology.customer.service;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.customer.payload.request.CustomerPreferenceRequest;

import java.util.UUID;

public interface CustomerPreferenceService {
    GlobalResponse create(CustomerPreferenceRequest request);
    GlobalResponse update(UUID id, CustomerPreferenceRequest request);
    GlobalResponse upsert(CustomerPreferenceRequest request);
    GlobalResponse getById(UUID id);
    GlobalResponse getByCustomerIdAndPreferenceKey(UUID customerId, String preferenceKey);
    GlobalResponse getByCustomerId(UUID customerId);
    GlobalResponse getByCustomerIdAndCategory(UUID customerId, String category);
    GlobalResponse getPreferencesAsMap(UUID customerId);
    GlobalResponse changeStatus(UUID id, StatusEnum status);
    GlobalResponse changeStatusByCustomerIdAndPreferenceKey(UUID customerId, String preferenceKey, StatusEnum status);
    GlobalResponse changeStatusByCustomerId(UUID customerId, StatusEnum status);
}
