package com.nepbrain.technology.communication.controller;

import com.nepbrain.technology.communication.annotation.CommunicationRestController;
import com.nepbrain.technology.communication.payload.request.SettingCategoryListRequest;
import com.nepbrain.technology.communication.payload.request.SettingCategoryRequest;
import com.nepbrain.technology.communication.service.SettingCategoryService;
import com.nepbrain.technology.communication.util.ReactiveBlocking;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CommunicationRestController
@RequiredArgsConstructor
@Tag(name = "Setting Category", description = "Setting category CRUD APIs")
public class SettingCategoryController {

    private final SettingCategoryService settingCategoryService;

    @PostMapping("/setting-category/create")
    @Operation(summary = "Create a new setting category")
    public Mono<ResponseEntity<GlobalResponse>> create(@Valid @RequestBody SettingCategoryRequest request) {
        return ReactiveBlocking.call(() -> {
            settingCategoryService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(GlobalResponseBuilder.buildSuccessResponse("Setting category created successfully."));
        });
    }

    @PutMapping("/setting-category/update")
    @Operation(summary = "Update an existing setting category")
    public Mono<ResponseEntity<GlobalResponse>> update(@RequestHeader UUID id, @Valid @RequestBody SettingCategoryRequest request) {
        return ReactiveBlocking.call(() -> {
            settingCategoryService.update(id, request);
            return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("Setting category updated successfully."));
        });
    }

    @GetMapping("/setting-category/get-by-id")
    @Operation(summary = "Get setting category by ID")
    public Mono<ResponseEntity<GlobalResponse>> getById(@RequestHeader UUID id) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(
                GlobalResponseBuilder.buildSuccessResponse(null, settingCategoryService.getById(id))));
    }

    @PostMapping("/setting-category/list")
    @Operation(summary = "List setting categories with pagination and filters")
    public Mono<ResponseEntity<GlobalResponse>> list(@Valid @RequestBody SettingCategoryListRequest request) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(
                GlobalResponseBuilder.buildSuccessResponse(null, settingCategoryService.list(request))));
    }

    @DeleteMapping("/setting-category/delete")
    @Operation(summary = "Delete a setting category")
    public Mono<ResponseEntity<GlobalResponse>> delete(@RequestHeader UUID id) {
        return ReactiveBlocking.call(() -> {
            settingCategoryService.delete(id);
            return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("Setting category deleted successfully."));
        });
    }
}
