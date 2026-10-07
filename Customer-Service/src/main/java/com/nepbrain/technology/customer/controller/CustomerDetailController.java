package com.nepbrain.technology.customer.controller;

import com.nepbrain.technology.customer.annotation.CustomCustomerRestController;
import com.nepbrain.technology.customer.payload.request.CustomerDetailRequest;
import com.nepbrain.technology.customer.payload.response.CustomerDetailResponse;
import com.nepbrain.technology.customer.service.CustomerDetailService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
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
@Tag(name = "Customer Detail Management", description = "APIs for managing customer details")
public class CustomerDetailController {

    private final CustomerDetailService detailService;

    @PostMapping("/customer-detail")
    @Operation(summary = "Create customer detail")
    public ResponseEntity<GlobalResponse> create(@Valid @RequestBody CustomerDetailRequest request) {
        CustomerDetailResponse response = detailService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_DETAIL_CREATED", response));
    }

    @PutMapping("/customer-detail/{id}")
    @Operation(summary = "Update customer detail")
    public ResponseEntity<GlobalResponse> update(@PathVariable UUID id, @Valid @RequestBody CustomerDetailRequest request) {
        CustomerDetailResponse response = detailService.update(id, request);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_DETAIL_UPDATED", response));
    }

    @PutMapping("/customer-detail/customer/{customerId}")
    @Operation(summary = "Update customer detail by customer ID")
    public ResponseEntity<GlobalResponse> updateByCustomerId(@PathVariable UUID customerId, @Valid @RequestBody CustomerDetailRequest request) {
        CustomerDetailResponse response = detailService.updateByCustomerId(customerId, request);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_DETAIL_UPDATED", response));
    }

    @GetMapping("/customer-detail/{id}")
    @Operation(summary = "Get customer detail by ID")
    public ResponseEntity<GlobalResponse> getById(@PathVariable UUID id) {
        CustomerDetailResponse response = detailService.getById(id);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_DETAIL_FETCHED", response));
    }

    @GetMapping("/customer-detail/customer/{customerId}")
    @Operation(summary = "Get customer detail by customer ID")
    public ResponseEntity<GlobalResponse> getByCustomerId(@PathVariable UUID customerId) {
        CustomerDetailResponse response = detailService.getByCustomerId(customerId);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_DETAIL_FETCHED", response));
    }

    @DeleteMapping("/customer-detail/{id}")
    @Operation(summary = "Delete customer detail")
    public ResponseEntity<GlobalResponse> delete(@PathVariable UUID id) {
        detailService.delete(id);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_DETAIL_DELETED", null));
    }

    @DeleteMapping("/customer-detail/customer/{customerId}")
    @Operation(summary = "Delete customer detail by customer ID")
    public ResponseEntity<GlobalResponse> deleteByCustomerId(@PathVariable UUID customerId) {
        detailService.deleteByCustomerId(customerId);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_DETAIL_DELETED", null));
    }
}

