package com.nepbrain.technology.communication.controller;

import com.nepbrain.technology.communication.annotation.CommunicationRestController;
import com.nepbrain.technology.communication.payload.request.SupportEmailDataRequest;
import com.nepbrain.technology.communication.payload.request.SupportEmailReplyRequest;
import com.nepbrain.technology.communication.payload.request.SupportEmailRequest;
import com.nepbrain.technology.communication.service.SupportEmailService;
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
@Tag(name = "Support Email", description = "Support email and reply APIs")
public class SupportEmailController {

    private final SupportEmailService supportEmailService;

    @PostMapping("/support-email/create")
    @Operation(summary = "Create a support email")
    public Mono<ResponseEntity<GlobalResponse>> create(@Valid @RequestBody SupportEmailRequest request) {
        return ReactiveBlocking.call(() -> ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseBuilder.buildSuccessResponse(null, supportEmailService.create(request))));
    }

    @PostMapping("/support-email/reply")
    @Operation(summary = "Add a reply to a support email")
    public Mono<ResponseEntity<GlobalResponse>> addReply(
            @RequestHeader UUID supportEmailId,
            @Valid @RequestBody SupportEmailReplyRequest request) {
        return ReactiveBlocking.call(() -> ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseBuilder.buildSuccessResponse(null, supportEmailService.addReply(supportEmailId, request))));
    }

    @GetMapping("/support-email/detail")
    @Operation(summary = "Get support email by ID with replies")
    public Mono<ResponseEntity<GlobalResponse>> getById(@RequestHeader UUID id) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(
                GlobalResponseBuilder.buildSuccessResponse(null, supportEmailService.getById(id))));
    }

    @PostMapping("/support-email/list")
    @Operation(summary = "List support emails with pagination")
    public Mono<ResponseEntity<GlobalResponse>> getAll(@Valid @RequestBody SupportEmailDataRequest request) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(
                GlobalResponseBuilder.buildSuccessResponse(null, supportEmailService.getAll(request))));
    }
}
