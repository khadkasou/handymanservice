package com.nepbrain.technology.customer.service;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.customer.payload.request.CustomerBankDetailRequest;

import java.util.UUID;

public interface CustomerBankDetailService {
    GlobalResponse create(CustomerBankDetailRequest request);
    GlobalResponse update(UUID id, CustomerBankDetailRequest request);
    GlobalResponse getById(UUID id);
    GlobalResponse getByCustomerId(UUID customerId);
    GlobalResponse getPrimaryByCustomerId(UUID customerId);
    GlobalResponse changeStatus(UUID id, StatusEnum status);
}
