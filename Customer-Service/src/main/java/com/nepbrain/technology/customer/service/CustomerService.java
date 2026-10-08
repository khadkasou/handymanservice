package com.nepbrain.technology.customer.service;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.payload.PaginationRequest;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.customer.payload.request.CustomerRequest;

import java.util.UUID;

public interface CustomerService {
    GlobalResponse create(CustomerRequest request);
    GlobalResponse update(UUID id, CustomerRequest request);
    GlobalResponse getById(UUID id);
    GlobalResponse getByCustomerCode(String customerCode);
    GlobalResponse getByEmail(String email);
    GlobalResponse getAll(PaginationRequest paginationRequest);
    GlobalResponse changeStatus(UUID id, StatusEnum status);
}
