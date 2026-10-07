package com.nepbrain.technology.customer.controller;

import com.nepbrain.technology.customer.annotation.CustomCustomerRestController;
import com.nepbrain.technology.customer.enums.AddressTypeEnum;
import com.nepbrain.technology.customer.payload.request.CustomerAddressRequest;
import com.nepbrain.technology.customer.payload.response.CustomerAddressResponse;
import com.nepbrain.technology.customer.service.CustomerAddressService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CustomCustomerRestController
@RequiredArgsConstructor
@Tag(name = "Customer Address Management", description = "APIs for managing customer addresses")
public class CustomerAddressController {

    private final CustomerAddressService addressService;

    @PostMapping("/customer-address")
    @Operation(summary = "Create customer address")
    public ResponseEntity<GlobalResponse> create(@Valid @RequestBody CustomerAddressRequest request) {
        CustomerAddressResponse response = addressService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_ADDRESS_CREATED", response));
    }

    @PutMapping("/customer-address/{id}")
    @Operation(summary = "Update customer address")
    public ResponseEntity<GlobalResponse> update(@PathVariable UUID id, @Valid @RequestBody CustomerAddressRequest request) {
        CustomerAddressResponse response = addressService.update(id, request);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_ADDRESS_UPDATED", response));
    }

    @GetMapping("/customer-address/{id}")
    @Operation(summary = "Get customer address by ID")
    public ResponseEntity<GlobalResponse> getById(@PathVariable UUID id) {
        CustomerAddressResponse response = addressService.getById(id);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_ADDRESS_FETCHED", response));
    }

    @GetMapping("/customer-address/customer/{customerId}")
    @Operation(summary = "Get all addresses for a customer")
    public ResponseEntity<GlobalResponse> getByCustomerId(@PathVariable UUID customerId) {
        List<CustomerAddressResponse> response = addressService.getByCustomerId(customerId);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_ADDRESSES_FETCHED", response));
    }

    @GetMapping("/customer-address/customer/{customerId}/type/{addressType}")
    @Operation(summary = "Get addresses by type for a customer")
    public ResponseEntity<GlobalResponse> getByCustomerIdAndAddressType(
            @PathVariable UUID customerId,
            @PathVariable String addressType) {
        try {
            AddressTypeEnum type = AddressTypeEnum.valueOf(addressType.toUpperCase());
            List<CustomerAddressResponse> response = addressService.getByCustomerIdAndAddressType(customerId, type);
            return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_ADDRESSES_FETCHED", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(GlobalResponseBuilder.buildFailResponse(new com.nepbrain.technology.core.exception.GlobalException("INVALID_ADDRESS_TYPE", org.springframework.http.HttpStatus.BAD_REQUEST)));
        }
    }

    @GetMapping("/customer-address/customer/{customerId}/primary")
    @Operation(summary = "Get primary address for a customer")
    public ResponseEntity<GlobalResponse> getPrimaryByCustomerId(@PathVariable UUID customerId) {
        CustomerAddressResponse response = addressService.getPrimaryByCustomerId(customerId);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_ADDRESS_FETCHED", response));
    }

    @DeleteMapping("/customer-address/{id}")
    @Operation(summary = "Delete customer address")
    public ResponseEntity<GlobalResponse> delete(@PathVariable UUID id) {
        addressService.delete(id);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_ADDRESS_DELETED", null));
    }
}

