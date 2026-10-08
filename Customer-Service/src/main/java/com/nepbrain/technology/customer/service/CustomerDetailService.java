package com.nepbrain.technology.customer.service;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.customer.payload.request.CustomerDetailRequest;

import java.util.UUID;

public interface CustomerDetailService {
    GlobalResponse create(CustomerDetailRequest request);
    GlobalResponse update(UUID id, CustomerDetailRequest request);
    GlobalResponse updateByCustomerId(UUID customerId, CustomerDetailRequest request);
    GlobalResponse getById(UUID id);
    GlobalResponse getByCustomerId(UUID customerId);
    GlobalResponse changeStatus(UUID id, StatusEnum status);
    GlobalResponse changeStatusByCustomerId(UUID customerId, StatusEnum status);
}
