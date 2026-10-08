package com.nepbrain.technology.communication.controller;

import com.nepbrain.technology.communication.annotation.CommunicationRestController;
import com.nepbrain.technology.communication.payload.request.SettingCategoryListRequest;
import com.nepbrain.technology.communication.payload.request.SettingCategoryRequest;
import com.nepbrain.technology.communication.service.SettingCategoryService;
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
@Tag(name = "Setting Category", description = "Setting category CRUD APIs")
public class SettingCategoryController {

    private final SettingCategoryService settingCategoryService;

    @PostMapping("/setting-category/create")
    @Operation(summary = "Create a new setting category")
    public ResponseEntity<GlobalResponse> create(@Valid @RequestBody SettingCategoryRequest request) {
        return ResponseEntity.ok().body(settingCategoryService.create(request));
    }

    @PutMapping("/setting-category/update")
    @Operation(summary = "Update an existing setting category")
    public ResponseEntity<GlobalResponse> update(@RequestHeader UUID id, @Valid @RequestBody SettingCategoryRequest request) {
        return ResponseEntity.ok().body(settingCategoryService.update(id, request));
    }

    @GetMapping("/setting-category/get-by-id")
    @Operation(summary = "Get setting category by ID")
    public ResponseEntity<GlobalResponse> getById(@RequestHeader UUID id) {
        return ResponseEntity.ok().body(settingCategoryService.getById(id));
    }

    @PostMapping("/setting-category/list")
    @Operation(summary = "List setting categories with pagination and filters")
    public ResponseEntity<GlobalResponse> list(@Valid @RequestBody SettingCategoryListRequest request) {
        return ResponseEntity.ok().body(settingCategoryService.list(request));
    }

    @DeleteMapping("/setting-category/delete")
    @Operation(summary = "Delete a setting category")
    public ResponseEntity<GlobalResponse> delete(@RequestHeader UUID id) {
        return ResponseEntity.ok().body(settingCategoryService.delete(id));
    }
}
