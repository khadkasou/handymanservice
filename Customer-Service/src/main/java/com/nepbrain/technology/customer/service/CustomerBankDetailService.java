package com.nepbrain.technology.customer.service;

import com.nepbrain.technology.customer.payload.request.CustomerBankDetailRequest;
import com.nepbrain.technology.customer.payload.response.CustomerBankDetailResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerBankDetailService {
    CustomerBankDetailResponse create(CustomerBankDetailRequest request);
    CustomerBankDetailResponse update(UUID id, CustomerBankDetailRequest request);
    CustomerBankDetailResponse getById(UUID id);
    List<CustomerBankDetailResponse> getByCustomerId(UUID customerId);
    CustomerBankDetailResponse getPrimaryByCustomerId(UUID customerId);
    void delete(UUID id);
}

