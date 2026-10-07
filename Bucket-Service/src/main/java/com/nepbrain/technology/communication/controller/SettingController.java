package com.nepbrain.technology.communication.controller;

import com.nepbrain.technology.communication.annotation.CommunicationRestController;
import com.nepbrain.technology.communication.payload.request.SettingListRequest;
import com.nepbrain.technology.communication.payload.request.SettingRequest;
import com.nepbrain.technology.communication.service.SettingService;
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
@Tag(name = "Setting", description = "Setting CRUD APIs")
public class SettingController {

    private final SettingService settingService;

    @PostMapping("/setting/create")
    @Operation(summary = "Create a new setting")
    public Mono<ResponseEntity<GlobalResponse>> create(@Valid @RequestBody SettingRequest request) {
        return ReactiveBlocking.call(() -> {
            settingService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(GlobalResponseBuilder.buildSuccessResponse("Setting created successfully."));
        });
    }

    @PutMapping("/setting/update")
    @Operation(summary = "Update an existing setting")
    public Mono<ResponseEntity<GlobalResponse>> update(@RequestHeader UUID id, @Valid @RequestBody SettingRequest request) {
        return ReactiveBlocking.call(() -> {
            settingService.update(id, request);
            return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("Setting updated successfully."));
        });
    }

    @GetMapping("/setting/get-by-id")
    @Operation(summary = "Get setting by ID")
    public Mono<ResponseEntity<GlobalResponse>> getById(@RequestHeader UUID id) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(
                GlobalResponseBuilder.buildSuccessResponse(null, settingService.getById(id))));
    }

    @PostMapping("/setting/list")
    @Operation(summary = "List settings with pagination and search")
    public Mono<ResponseEntity<GlobalResponse>> list(@Valid @RequestBody SettingListRequest request) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(
                GlobalResponseBuilder.buildSuccessResponse(null, settingService.list(request))));
    }

    @DeleteMapping("/setting/delete")
    @Operation(summary = "Delete a setting")
    public Mono<ResponseEntity<GlobalResponse>> delete(@RequestHeader UUID id) {
        return ReactiveBlocking.call(() -> {
            settingService.delete(id);
            return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("Setting deleted successfully."));
        });
    }
}
