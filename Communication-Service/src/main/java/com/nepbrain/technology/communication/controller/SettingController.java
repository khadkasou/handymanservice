package com.nepbrain.technology.communication.controller;

import com.nepbrain.technology.communication.annotation.CommunicationRestController;
import com.nepbrain.technology.communication.payload.request.SettingListRequest;
import com.nepbrain.technology.communication.payload.request.SettingRequest;
import com.nepbrain.technology.communication.service.SettingService;
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
@Tag(name = "Setting", description = "Setting CRUD APIs")
public class SettingController {

    private final SettingService settingService;

    @PostMapping("/setting/create")
    @Operation(summary = "Create a new setting")
    public ResponseEntity<GlobalResponse> create(@Valid @RequestBody SettingRequest request) {
        return ResponseEntity.ok().body(settingService.create(request));
    }

    @PutMapping("/setting/update")
    @Operation(summary = "Update an existing setting")
    public ResponseEntity<GlobalResponse> update(@RequestHeader UUID id, @Valid @RequestBody SettingRequest request) {
        return ResponseEntity.ok().body(settingService.update(id, request));
    }

    @GetMapping("/setting/get-by-id")
    @Operation(summary = "Get setting by ID")
    public ResponseEntity<GlobalResponse> getById(@RequestHeader UUID id) {
        return ResponseEntity.ok().body(settingService.getById(id));
    }

    @PostMapping("/setting/list")
    @Operation(summary = "List settings with pagination and search")
    public ResponseEntity<GlobalResponse> list(@Valid @RequestBody SettingListRequest request) {
        return ResponseEntity.ok().body(settingService.list(request));
    }

    @DeleteMapping("/setting/delete")
    @Operation(summary = "Delete a setting")
    public ResponseEntity<GlobalResponse> delete(@RequestHeader UUID id) {
        return ResponseEntity.ok().body(settingService.delete(id));
    }
}
