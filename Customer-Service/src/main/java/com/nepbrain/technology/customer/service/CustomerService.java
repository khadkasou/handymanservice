package com.nepbrain.technology.customer.service;

import com.nepbrain.technology.core.payload.PaginationRequest;
import com.nepbrain.technology.core.payload.response.PaginationResponse;
import com.nepbrain.technology.customer.payload.request.CustomerRequest;
import com.nepbrain.technology.customer.payload.response.CustomerResponse;

import java.util.UUID;

public interface CustomerService {
    CustomerResponse create(CustomerRequest request);
    CustomerResponse update(UUID id, CustomerRequest request);
    CustomerResponse getById(UUID id);
    CustomerResponse getByCustomerCode(String customerCode);
    CustomerResponse getByEmail(String email);
    PaginationResponse getAll(PaginationRequest paginationRequest);
    void delete(UUID id);
}

