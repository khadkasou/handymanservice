package com.nepbrain.technology.customer.controller;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.customer.annotation.CustomCustomerRestController;
import com.nepbrain.technology.customer.payload.request.CustomerAddressRequest;
import com.nepbrain.technology.customer.service.CustomerAddressService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CustomCustomerRestController
@RequiredArgsConstructor
@Tag(name = "Customer Address Management", description = "APIs for managing customer addresses")
public class CustomerAddressController {

    private final CustomerAddressService addressService;

    @PostMapping("/customer-address")
    @Operation(summary = "Create customer address")
    public ResponseEntity<GlobalResponse> create(@Valid @RequestBody CustomerAddressRequest request) {
        return ResponseEntity.ok().body(addressService.create(request));
    }

    @PutMapping("/customer-address/{id}")
    @Operation(summary = "Update customer address")
    public ResponseEntity<GlobalResponse> update(@PathVariable UUID id, @Valid @RequestBody CustomerAddressRequest request) {
        return ResponseEntity.ok().body(addressService.update(id, request));
    }

    @GetMapping("/customer-address/{id}")
    @Operation(summary = "Get customer address by ID")
    public ResponseEntity<GlobalResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(addressService.getById(id));
    }

    @GetMapping("/customer-address/customer/{customerId}")
    @Operation(summary = "Get all addresses for a customer")
    public ResponseEntity<GlobalResponse> getByCustomerId(@PathVariable UUID customerId) {
        return ResponseEntity.ok().body(addressService.getByCustomerId(customerId));
    }

    @GetMapping("/customer-address/customer/{customerId}/type/{addressType}")
    @Operation(summary = "Get addresses by type for a customer")
    public ResponseEntity<GlobalResponse> getByCustomerIdAndAddressType(
            @PathVariable UUID customerId,
            @PathVariable String addressType) {
        return ResponseEntity.ok().body(addressService.getByCustomerIdAndAddressType(customerId, addressType));
    }

    @GetMapping("/customer-address/customer/{customerId}/primary")
    @Operation(summary = "Get primary address for a customer")
    public ResponseEntity<GlobalResponse> getPrimaryByCustomerId(@PathVariable UUID customerId) {
        return ResponseEntity.ok().body(addressService.getPrimaryByCustomerId(customerId));
    }

    @PutMapping("/customer-address/{id}/status")
    @Operation(summary = "Change customer address status")
    public ResponseEntity<GlobalResponse> changeStatus(@PathVariable UUID id, @RequestParam StatusEnum status) {
        return ResponseEntity.ok().body(addressService.changeStatus(id, status));
    }
}
