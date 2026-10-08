package com.nepbrain.technology.communication.controller;

import com.nepbrain.technology.communication.annotation.CommunicationRestController;
import com.nepbrain.technology.communication.payload.request.SettingDetailBulkUpdateRequest;
import com.nepbrain.technology.communication.payload.request.SettingDetailListRequest;
import com.nepbrain.technology.communication.payload.request.SettingDetailRequest;
import com.nepbrain.technology.communication.service.SettingDetailService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CommunicationRestController
@RequiredArgsConstructor
@Tag(name = "Setting Detail", description = "Setting detail CRUD APIs")
public class SettingDetailController {

    private final SettingDetailService settingDetailService;

    @PostMapping("/setting-detail/create")
    @Operation(summary = "Create a new setting detail")
    public ResponseEntity<GlobalResponse> create(@Valid @RequestBody SettingDetailRequest request) {
        return ResponseEntity.ok().body(settingDetailService.create(request));
    }

    @PutMapping("/setting-detail/update")
    @Operation(summary = "Update an existing setting detail")
    public ResponseEntity<GlobalResponse> update(@RequestHeader UUID id, @Valid @RequestBody SettingDetailRequest request) {
        return ResponseEntity.ok().body(settingDetailService.update(id, request));
    }

    @GetMapping("/setting-detail/get-by-id")
    @Operation(summary = "Get setting detail by ID")
    public ResponseEntity<GlobalResponse> getById(@RequestHeader UUID id) {
        return ResponseEntity.ok().body(settingDetailService.getById(id));
    }

    @GetMapping("/setting-detail/get-by-key")
    @Operation(summary = "Get setting detail by key (e.g. TRIAL_PERIOD)")
    public ResponseEntity<GlobalResponse> getByKey(@RequestHeader("key") String key) {
        return ResponseEntity.ok().body(settingDetailService.getByKey(key));
    }

    @PostMapping("/setting-detail/list")
    @Operation(summary = "List setting details with pagination and filters")
    public ResponseEntity<GlobalResponse> list(@Valid @RequestBody SettingDetailListRequest request) {
        return ResponseEntity.ok().body(settingDetailService.list(request));
    }

    @DeleteMapping("/setting-detail/delete")
    @Operation(summary = "Delete a setting detail")
    public ResponseEntity<GlobalResponse> delete(@RequestHeader UUID id) {
        return ResponseEntity.ok().body(settingDetailService.delete(id));
    }

    @PutMapping("/setting-detail/bulk-update")
    @Operation(summary = "Bulk update setting details by setting category ID")
    public ResponseEntity<GlobalResponse> bulkUpdate(@Valid @RequestBody SettingDetailBulkUpdateRequest request) {
        return ResponseEntity.ok().body(settingDetailService.bulkUpdateBySettingCategoryId(request));
    }
}
