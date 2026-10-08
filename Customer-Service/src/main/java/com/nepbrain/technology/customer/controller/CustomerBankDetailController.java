package com.nepbrain.technology.customer.controller;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.customer.annotation.CustomCustomerRestController;
import com.nepbrain.technology.customer.payload.request.CustomerBankDetailRequest;
import com.nepbrain.technology.customer.service.CustomerBankDetailService;
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
@Tag(name = "Customer Bank Detail Management", description = "APIs for managing customer bank details")
public class CustomerBankDetailController {

    private final CustomerBankDetailService bankDetailService;

    @PostMapping("/customer-bank-detail")
    @Operation(summary = "Create customer bank detail")
    public ResponseEntity<GlobalResponse> create(@Valid @RequestBody CustomerBankDetailRequest request) {
        return ResponseEntity.ok().body(bankDetailService.create(request));
    }

    @PutMapping("/customer-bank-detail/{id}")
    @Operation(summary = "Update customer bank detail")
    public ResponseEntity<GlobalResponse> update(@PathVariable UUID id, @Valid @RequestBody CustomerBankDetailRequest request) {
        return ResponseEntity.ok().body(bankDetailService.update(id, request));
    }

    @GetMapping("/customer-bank-detail/{id}")
    @Operation(summary = "Get customer bank detail by ID")
    public ResponseEntity<GlobalResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(bankDetailService.getById(id));
    }

    @GetMapping("/customer-bank-detail/customer/{customerId}")
    @Operation(summary = "Get all bank details for a customer")
    public ResponseEntity<GlobalResponse> getByCustomerId(@PathVariable UUID customerId) {
        return ResponseEntity.ok().body(bankDetailService.getByCustomerId(customerId));
    }

    @GetMapping("/customer-bank-detail/customer/{customerId}/primary")
    @Operation(summary = "Get primary bank detail for a customer")
    public ResponseEntity<GlobalResponse> getPrimaryByCustomerId(@PathVariable UUID customerId) {
        return ResponseEntity.ok().body(bankDetailService.getPrimaryByCustomerId(customerId));
    }

    @PutMapping("/customer-bank-detail/{id}/status")
    @Operation(summary = "Change customer bank detail status")
    public ResponseEntity<GlobalResponse> changeStatus(@PathVariable UUID id, @RequestParam StatusEnum status) {
        return ResponseEntity.ok().body(bankDetailService.changeStatus(id, status));
    }
}
