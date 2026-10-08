package com.nepbrain.technology.customer.controller;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.customer.annotation.CustomCustomerRestController;
import com.nepbrain.technology.customer.payload.request.CustomerPreferenceRequest;
import com.nepbrain.technology.customer.service.CustomerPreferenceService;
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
@Tag(name = "Customer Preference Management", description = "APIs for managing customer preferences")
public class CustomerPreferenceController {

    private final CustomerPreferenceService preferenceService;

    @PostMapping("/customer-preference")
    @Operation(summary = "Create customer preference")
    public ResponseEntity<GlobalResponse> create(@Valid @RequestBody CustomerPreferenceRequest request) {
        return ResponseEntity.ok().body(preferenceService.create(request));
    }

    @PutMapping("/customer-preference/{id}")
    @Operation(summary = "Update customer preference")
    public ResponseEntity<GlobalResponse> update(@PathVariable UUID id, @Valid @RequestBody CustomerPreferenceRequest request) {
        return ResponseEntity.ok().body(preferenceService.update(id, request));
    }

    @PutMapping("/customer-preference/upsert")
    @Operation(summary = "Create or update customer preference (upsert)")
    public ResponseEntity<GlobalResponse> upsert(@Valid @RequestBody CustomerPreferenceRequest request) {
        return ResponseEntity.ok().body(preferenceService.upsert(request));
    }

    @GetMapping("/customer-preference/{id}")
    @Operation(summary = "Get customer preference by ID")
    public ResponseEntity<GlobalResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(preferenceService.getById(id));
    }

    @GetMapping("/customer-preference/customer/{customerId}/key/{preferenceKey}")
    @Operation(summary = "Get customer preference by customer ID and preference key")
    public ResponseEntity<GlobalResponse> getByCustomerIdAndPreferenceKey(
            @PathVariable UUID customerId,
            @PathVariable String preferenceKey) {
        return ResponseEntity.ok().body(preferenceService.getByCustomerIdAndPreferenceKey(customerId, preferenceKey));
    }

    @GetMapping("/customer-preference/customer/{customerId}")
    @Operation(summary = "Get all preferences for a customer")
    public ResponseEntity<GlobalResponse> getByCustomerId(@PathVariable UUID customerId) {
        return ResponseEntity.ok().body(preferenceService.getByCustomerId(customerId));
    }

    @GetMapping("/customer-preference/customer/{customerId}/category/{category}")
    @Operation(summary = "Get preferences by category for a customer")
    public ResponseEntity<GlobalResponse> getByCustomerIdAndCategory(
            @PathVariable UUID customerId,
            @PathVariable String category) {
        return ResponseEntity.ok().body(preferenceService.getByCustomerIdAndCategory(customerId, category));
    }

    @GetMapping("/customer-preference/customer/{customerId}/map")
    @Operation(summary = "Get all preferences as a map (key-value pairs)")
    public ResponseEntity<GlobalResponse> getPreferencesAsMap(@PathVariable UUID customerId) {
        return ResponseEntity.ok().body(preferenceService.getPreferencesAsMap(customerId));
    }

    @PutMapping("/customer-preference/{id}/status")
    @Operation(summary = "Change customer preference status")
    public ResponseEntity<GlobalResponse> changeStatus(@PathVariable UUID id, @RequestParam StatusEnum status) {
        return ResponseEntity.ok().body(preferenceService.changeStatus(id, status));
    }

    @PutMapping("/customer-preference/customer/{customerId}/key/{preferenceKey}/status")
    @Operation(summary = "Change customer preference status by customer ID and preference key")
    public ResponseEntity<GlobalResponse> changeStatusByCustomerIdAndPreferenceKey(
            @PathVariable UUID customerId,
            @PathVariable String preferenceKey,
            @RequestParam StatusEnum status) {
        return ResponseEntity.ok().body(preferenceService.changeStatusByCustomerIdAndPreferenceKey(customerId, preferenceKey, status));
    }

    @PutMapping("/customer-preference/customer/{customerId}/status")
    @Operation(summary = "Change status of all preferences for a customer")
    public ResponseEntity<GlobalResponse> changeStatusByCustomerId(@PathVariable UUID customerId, @RequestParam StatusEnum status) {
        return ResponseEntity.ok().body(preferenceService.changeStatusByCustomerId(customerId, status));
    }
}
