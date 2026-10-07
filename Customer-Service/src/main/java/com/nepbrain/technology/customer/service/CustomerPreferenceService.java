package com.nepbrain.technology.customer.service;

import com.nepbrain.technology.customer.payload.request.CustomerPreferenceRequest;
import com.nepbrain.technology.customer.payload.response.CustomerPreferenceResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CustomerPreferenceService {
    CustomerPreferenceResponse create(CustomerPreferenceRequest request);
    CustomerPreferenceResponse update(UUID id, CustomerPreferenceRequest request);
    CustomerPreferenceResponse upsert(CustomerPreferenceRequest request); // Create or update by customerId and preferenceKey
    CustomerPreferenceResponse getById(UUID id);
    CustomerPreferenceResponse getByCustomerIdAndPreferenceKey(UUID customerId, String preferenceKey);
    List<CustomerPreferenceResponse> getByCustomerId(UUID customerId);
    List<CustomerPreferenceResponse> getByCustomerIdAndCategory(UUID customerId, String category);
    Map<String, String> getPreferencesAsMap(UUID customerId);
    void delete(UUID id);
    void deleteByCustomerIdAndPreferenceKey(UUID customerId, String preferenceKey);
    void deleteAllByCustomerId(UUID customerId);
}

