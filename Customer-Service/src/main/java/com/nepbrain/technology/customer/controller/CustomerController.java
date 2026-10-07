package com.nepbrain.technology.customer.controller;

import com.nepbrain.technology.customer.annotation.CustomCustomerRestController;
import com.nepbrain.technology.customer.payload.request.CustomerRequest;
import com.nepbrain.technology.customer.payload.response.CustomerResponse;
import com.nepbrain.technology.customer.service.CustomerService;
import com.nepbrain.technology.core.payload.PaginationRequest;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import com.nepbrain.technology.core.payload.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CustomCustomerRestController
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for managing customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/customer")
    @Operation(summary = "Create a new customer")
    public ResponseEntity<GlobalResponse> create(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_CREATED", response));
    }

    @PutMapping("/customer/{id}")
    @Operation(summary = "Update a customer")
    public ResponseEntity<GlobalResponse> update(@PathVariable UUID id, @Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.update(id, request);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_UPDATED", response));
    }

    @GetMapping("/customer/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<GlobalResponse> getById(@PathVariable UUID id) {
        CustomerResponse response = customerService.getById(id);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_FETCHED", response));
    }

    @GetMapping("/customer/code/{customerCode}")
    @Operation(summary = "Get customer by customer code")
    public ResponseEntity<GlobalResponse> getByCustomerCode(@PathVariable String customerCode) {
        CustomerResponse response = customerService.getByCustomerCode(customerCode);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_FETCHED", response));
    }

    @GetMapping("/customer/email/{email}")
    @Operation(summary = "Get customer by email")
    public ResponseEntity<GlobalResponse> getByEmail(@PathVariable String email) {
        CustomerResponse response = customerService.getByEmail(email);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_FETCHED", response));
    }

    @GetMapping("/customer")
    @Operation(summary = "Get all customers with pagination")
    public ResponseEntity<GlobalResponse> getAll(@Valid PaginationRequest paginationRequest) {
        PaginationResponse response = customerService.getAll(paginationRequest);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMERS_FETCHED", response));
    }

    @DeleteMapping("/customer/{id}")
    @Operation(summary = "Delete a customer")
    public ResponseEntity<GlobalResponse> delete(@PathVariable UUID id) {
        customerService.delete(id);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_DELETED", null));
    }
}

