package com.nepbrain.technology.customer.controller;

import com.nepbrain.technology.customer.annotation.CustomCustomerRestController;
import com.nepbrain.technology.customer.payload.request.CustomerBankDetailRequest;
import com.nepbrain.technology.customer.payload.response.CustomerBankDetailResponse;
import com.nepbrain.technology.customer.service.CustomerBankDetailService;
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
@Tag(name = "Customer Bank Detail Management", description = "APIs for managing customer bank details")
public class CustomerBankDetailController {

    private final CustomerBankDetailService bankDetailService;

    @PostMapping("/customer-bank-detail")
    @Operation(summary = "Create customer bank detail")
    public ResponseEntity<GlobalResponse> create(@Valid @RequestBody CustomerBankDetailRequest request) {
        CustomerBankDetailResponse response = bankDetailService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_BANK_DETAIL_CREATED", response));
    }

    @PutMapping("/customer-bank-detail/{id}")
    @Operation(summary = "Update customer bank detail")
    public ResponseEntity<GlobalResponse> update(@PathVariable UUID id, @Valid @RequestBody CustomerBankDetailRequest request) {
        CustomerBankDetailResponse response = bankDetailService.update(id, request);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_BANK_DETAIL_UPDATED", response));
    }

    @GetMapping("/customer-bank-detail/{id}")
    @Operation(summary = "Get customer bank detail by ID")
    public ResponseEntity<GlobalResponse> getById(@PathVariable UUID id) {
        CustomerBankDetailResponse response = bankDetailService.getById(id);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_BANK_DETAIL_FETCHED", response));
    }

    @GetMapping("/customer-bank-detail/customer/{customerId}")
    @Operation(summary = "Get all bank details for a customer")
    public ResponseEntity<GlobalResponse> getByCustomerId(@PathVariable UUID customerId) {
        List<CustomerBankDetailResponse> response = bankDetailService.getByCustomerId(customerId);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_BANK_DETAILS_FETCHED", response));
    }

    @GetMapping("/customer-bank-detail/customer/{customerId}/primary")
    @Operation(summary = "Get primary bank detail for a customer")
    public ResponseEntity<GlobalResponse> getPrimaryByCustomerId(@PathVariable UUID customerId) {
        CustomerBankDetailResponse response = bankDetailService.getPrimaryByCustomerId(customerId);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_BANK_DETAIL_FETCHED", response));
    }

    @DeleteMapping("/customer-bank-detail/{id}")
    @Operation(summary = "Delete customer bank detail")
    public ResponseEntity<GlobalResponse> delete(@PathVariable UUID id) {
        bankDetailService.delete(id);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_BANK_DETAIL_DELETED", null));
    }
}

