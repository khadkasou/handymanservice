package com.nepbrain.technology.customer.service;

import com.nepbrain.technology.customer.enums.AddressTypeEnum;
import com.nepbrain.technology.customer.payload.request.CustomerAddressRequest;
import com.nepbrain.technology.customer.payload.response.CustomerAddressResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerAddressService {
    CustomerAddressResponse create(CustomerAddressRequest request);
    CustomerAddressResponse update(UUID id, CustomerAddressRequest request);
    CustomerAddressResponse getById(UUID id);
    List<CustomerAddressResponse> getByCustomerId(UUID customerId);
    List<CustomerAddressResponse> getByCustomerIdAndAddressType(UUID customerId, AddressTypeEnum addressType);
    CustomerAddressResponse getPrimaryByCustomerId(UUID customerId);
    void delete(UUID id);
}

