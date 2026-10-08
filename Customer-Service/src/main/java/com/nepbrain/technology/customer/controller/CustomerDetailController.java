package com.nepbrain.technology.customer.controller;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.customer.annotation.CustomCustomerRestController;
import com.nepbrain.technology.customer.payload.request.CustomerDetailRequest;
import com.nepbrain.technology.customer.service.CustomerDetailService;
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
@Tag(name = "Customer Detail Management", description = "APIs for managing customer details")
public class CustomerDetailController {

    private final CustomerDetailService detailService;

    @PostMapping("/customer-detail")
    @Operation(summary = "Create customer detail")
    public ResponseEntity<GlobalResponse> create(@Valid @RequestBody CustomerDetailRequest request) {
        return ResponseEntity.ok().body(detailService.create(request));
    }

    @PutMapping("/customer-detail/{id}")
    @Operation(summary = "Update customer detail")
    public ResponseEntity<GlobalResponse> update(@PathVariable UUID id, @Valid @RequestBody CustomerDetailRequest request) {
        return ResponseEntity.ok().body(detailService.update(id, request));
    }

    @PutMapping("/customer-detail/customer/{customerId}")
    @Operation(summary = "Update customer detail by customer ID")
    public ResponseEntity<GlobalResponse> updateByCustomerId(@PathVariable UUID customerId, @Valid @RequestBody CustomerDetailRequest request) {
        return ResponseEntity.ok().body(detailService.updateByCustomerId(customerId, request));
    }

    @GetMapping("/customer-detail/{id}")
    @Operation(summary = "Get customer detail by ID")
    public ResponseEntity<GlobalResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(detailService.getById(id));
    }

    @GetMapping("/customer-detail/customer/{customerId}")
    @Operation(summary = "Get customer detail by customer ID")
    public ResponseEntity<GlobalResponse> getByCustomerId(@PathVariable UUID customerId) {
        return ResponseEntity.ok().body(detailService.getByCustomerId(customerId));
    }

    @PutMapping("/customer-detail/{id}/status")
    @Operation(summary = "Change customer detail status")
    public ResponseEntity<GlobalResponse> changeStatus(@PathVariable UUID id, @RequestParam StatusEnum status) {
        return ResponseEntity.ok().body(detailService.changeStatus(id, status));
    }

    @PutMapping("/customer-detail/customer/{customerId}/status")
    @Operation(summary = "Change customer detail status by customer ID")
    public ResponseEntity<GlobalResponse> changeStatusByCustomerId(@PathVariable UUID customerId, @RequestParam StatusEnum status) {
        return ResponseEntity.ok().body(detailService.changeStatusByCustomerId(customerId, status));
    }
}
