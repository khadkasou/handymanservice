package com.nepbrain.technology.customer.controller;

import com.nepbrain.technology.customer.annotation.CustomCustomerRestController;
import com.nepbrain.technology.customer.payload.request.CustomerPreferenceRequest;
import com.nepbrain.technology.customer.payload.response.CustomerPreferenceResponse;
import com.nepbrain.technology.customer.service.CustomerPreferenceService;
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
import java.util.Map;
import java.util.UUID;

@CustomCustomerRestController
@RequiredArgsConstructor
@Tag(name = "Customer Preference Management", description = "APIs for managing customer preferences")
public class CustomerPreferenceController {

    private final CustomerPreferenceService preferenceService;

    @PostMapping("/customer-preference")
    @Operation(summary = "Create customer preference")
    public ResponseEntity<GlobalResponse> create(@Valid @RequestBody CustomerPreferenceRequest request) {
        CustomerPreferenceResponse response = preferenceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_PREFERENCE_CREATED", response));
    }

    @PutMapping("/customer-preference/{id}")
    @Operation(summary = "Update customer preference")
    public ResponseEntity<GlobalResponse> update(@PathVariable UUID id, @Valid @RequestBody CustomerPreferenceRequest request) {
        CustomerPreferenceResponse response = preferenceService.update(id, request);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_PREFERENCE_UPDATED", response));
    }

    @PutMapping("/customer-preference/upsert")
    @Operation(summary = "Create or update customer preference (upsert)")
    public ResponseEntity<GlobalResponse> upsert(@Valid @RequestBody CustomerPreferenceRequest request) {
        CustomerPreferenceResponse response = preferenceService.upsert(request);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_PREFERENCE_UPSERTED", response));
    }

    @GetMapping("/customer-preference/{id}")
    @Operation(summary = "Get customer preference by ID")
    public ResponseEntity<GlobalResponse> getById(@PathVariable UUID id) {
        CustomerPreferenceResponse response = preferenceService.getById(id);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_PREFERENCE_FETCHED", response));
    }

    @GetMapping("/customer-preference/customer/{customerId}/key/{preferenceKey}")
    @Operation(summary = "Get customer preference by customer ID and preference key")
    public ResponseEntity<GlobalResponse> getByCustomerIdAndPreferenceKey(
            @PathVariable UUID customerId,
            @PathVariable String preferenceKey) {
        CustomerPreferenceResponse response = preferenceService.getByCustomerIdAndPreferenceKey(customerId, preferenceKey);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_PREFERENCE_FETCHED", response));
    }

    @GetMapping("/customer-preference/customer/{customerId}")
    @Operation(summary = "Get all preferences for a customer")
    public ResponseEntity<GlobalResponse> getByCustomerId(@PathVariable UUID customerId) {
        List<CustomerPreferenceResponse> response = preferenceService.getByCustomerId(customerId);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_PREFERENCES_FETCHED", response));
    }

    @GetMapping("/customer-preference/customer/{customerId}/category/{category}")
    @Operation(summary = "Get preferences by category for a customer")
    public ResponseEntity<GlobalResponse> getByCustomerIdAndCategory(
            @PathVariable UUID customerId,
            @PathVariable String category) {
        List<CustomerPreferenceResponse> response = preferenceService.getByCustomerIdAndCategory(customerId, category);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_PREFERENCES_FETCHED", response));
    }

    @GetMapping("/customer-preference/customer/{customerId}/map")
    @Operation(summary = "Get all preferences as a map (key-value pairs)")
    public ResponseEntity<GlobalResponse> getPreferencesAsMap(@PathVariable UUID customerId) {
        Map<String, String> response = preferenceService.getPreferencesAsMap(customerId);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_PREFERENCES_FETCHED", response));
    }

    @DeleteMapping("/customer-preference/{id}")
    @Operation(summary = "Delete customer preference")
    public ResponseEntity<GlobalResponse> delete(@PathVariable UUID id) {
        preferenceService.delete(id);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_PREFERENCE_DELETED", null));
    }

    @DeleteMapping("/customer-preference/customer/{customerId}/key/{preferenceKey}")
    @Operation(summary = "Delete customer preference by customer ID and preference key")
    public ResponseEntity<GlobalResponse> deleteByCustomerIdAndPreferenceKey(
            @PathVariable UUID customerId,
            @PathVariable String preferenceKey) {
        preferenceService.deleteByCustomerIdAndPreferenceKey(customerId, preferenceKey);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_PREFERENCE_DELETED", null));
    }

    @DeleteMapping("/customer-preference/customer/{customerId}/all")
    @Operation(summary = "Delete all preferences for a customer")
    public ResponseEntity<GlobalResponse> deleteAllByCustomerId(@PathVariable UUID customerId) {
        preferenceService.deleteAllByCustomerId(customerId);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CUSTOMER_PREFERENCES_DELETED", null));
    }
}

