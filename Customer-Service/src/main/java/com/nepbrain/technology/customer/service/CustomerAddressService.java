package com.nepbrain.technology.customer.service;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.customer.payload.request.CustomerAddressRequest;

import java.util.UUID;

public interface CustomerAddressService {
    GlobalResponse create(CustomerAddressRequest request);
    GlobalResponse update(UUID id, CustomerAddressRequest request);
    GlobalResponse getById(UUID id);
    GlobalResponse getByCustomerId(UUID customerId);
    GlobalResponse getByCustomerIdAndAddressType(UUID customerId, String addressType);
    GlobalResponse getPrimaryByCustomerId(UUID customerId);
    GlobalResponse changeStatus(UUID id, StatusEnum status);
}
