package com.nepbrain.technology.customer.controller;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.customer.annotation.CustomCustomerRestController;
import com.nepbrain.technology.customer.payload.request.CustomerRequest;
import com.nepbrain.technology.customer.service.CustomerService;
import com.nepbrain.technology.core.payload.PaginationRequest;
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
@Tag(name = "Customer Management", description = "APIs for managing customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/customer")
    @Operation(summary = "Create a new customer")
    public ResponseEntity<GlobalResponse> create(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok().body(customerService.create(request));
    }

    @PutMapping("/customer/{id}")
    @Operation(summary = "Update a customer")
    public ResponseEntity<GlobalResponse> update(@PathVariable UUID id, @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok().body(customerService.update(id, request));
    }

    @GetMapping("/customer/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<GlobalResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(customerService.getById(id));
    }

    @GetMapping("/customer/code/{customerCode}")
    @Operation(summary = "Get customer by customer code")
    public ResponseEntity<GlobalResponse> getByCustomerCode(@PathVariable String customerCode) {
        return ResponseEntity.ok().body(customerService.getByCustomerCode(customerCode));
    }

    @GetMapping("/customer/email/{email}")
    @Operation(summary = "Get customer by email")
    public ResponseEntity<GlobalResponse> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok().body(customerService.getByEmail(email));
    }

    @GetMapping("/customer")
    @Operation(summary = "Get all customers with pagination")
    public ResponseEntity<GlobalResponse> getAll(@Valid PaginationRequest paginationRequest) {
        return ResponseEntity.ok().body(customerService.getAll(paginationRequest));
    }

    @PutMapping("/customer/{id}/status")
    @Operation(summary = "Change customer status")
    public ResponseEntity<GlobalResponse> changeStatus(@PathVariable UUID id, @RequestParam StatusEnum status) {
        return ResponseEntity.ok().body(customerService.changeStatus(id, status));
    }
}
