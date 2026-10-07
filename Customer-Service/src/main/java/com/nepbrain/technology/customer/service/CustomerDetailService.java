package com.nepbrain.technology.customer.service;

import com.nepbrain.technology.customer.payload.request.CustomerDetailRequest;
import com.nepbrain.technology.customer.payload.response.CustomerDetailResponse;

import java.util.UUID;

public interface CustomerDetailService {
    CustomerDetailResponse create(CustomerDetailRequest request);
    CustomerDetailResponse update(UUID id, CustomerDetailRequest request);
    CustomerDetailResponse updateByCustomerId(UUID customerId, CustomerDetailRequest request);
    CustomerDetailResponse getById(UUID id);
    CustomerDetailResponse getByCustomerId(UUID customerId);
    void delete(UUID id);
    void deleteByCustomerId(UUID customerId);
}

