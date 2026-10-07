package com.nepbrain.technology.communication.controller;

import com.nepbrain.technology.communication.annotation.CommunicationRestController;
import com.nepbrain.technology.communication.enums.StatusEnum;
import com.nepbrain.technology.communication.payload.request.BulkSendRequest;
import com.nepbrain.technology.communication.payload.request.MessageDataRequest;
import com.nepbrain.technology.communication.payload.request.MessageDetailRequest;
import com.nepbrain.technology.communication.payload.request.MessageRequest;
import com.nepbrain.technology.communication.service.BulkSendService;
import com.nepbrain.technology.communication.service.MessageService;
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
@Tag(name = "Message", description = "Message and message detail CRUD APIs")
public class MessageController {

    private final MessageService messageService;
    private final BulkSendService bulkSendService;

    @PostMapping("/message/create")
    @Operation(summary = "Create a new message with details")
    public Mono<ResponseEntity<GlobalResponse>> create(@Valid @RequestBody MessageRequest request) {
        return ReactiveBlocking.call(() -> {
            messageService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(GlobalResponseBuilder.buildSuccessResponse("Message created successfully."));
        });
    }

    @PutMapping("/message/update")
    @Operation(summary = "Update an existing message")
    public Mono<ResponseEntity<GlobalResponse>> update(@RequestHeader UUID id, @Valid @RequestBody MessageRequest request) {
        return ReactiveBlocking.call(() -> {
            request.setId(id);
            messageService.update(request);
            return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("Message updated successfully."));
        });
    }

    @GetMapping("/message/detail")
    @Operation(summary = "Get message by ID")
    public Mono<ResponseEntity<GlobalResponse>> getById(@RequestHeader UUID id) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(
                GlobalResponseBuilder.buildSuccessResponse(null, messageService.getById(id))));
    }

    @PostMapping("/message/list")
    @Operation(summary = "List messages with pagination and filters")
    public Mono<ResponseEntity<GlobalResponse>> getAll(@Valid @RequestBody MessageDataRequest request) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(
                GlobalResponseBuilder.buildSuccessResponse(null, messageService.getAll(request))));
    }

    @DeleteMapping("/message/delete")
    @Operation(summary = "Delete message by ID")
    public Mono<ResponseEntity<GlobalResponse>> delete(@RequestHeader UUID id) {
        return ReactiveBlocking.call(() -> {
            messageService.delete(id);
            return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("Message deleted successfully."));
        });
    }

    @PutMapping("/message/change-status")
    @Operation(summary = "Change message status")
    public Mono<ResponseEntity<GlobalResponse>> changeStatus(@RequestHeader UUID id, @RequestHeader StatusEnum status) {
        return ReactiveBlocking.call(() -> {
            messageService.changeStatus(id, status);
            return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("Message status changed successfully."));
        });
    }

    @PostMapping("/message-detail/create")
    @Operation(summary = "Add or update a message detail by language")
    public Mono<ResponseEntity<GlobalResponse>> addDetail(
            @RequestHeader UUID messageId,
            @Valid @RequestBody MessageDetailRequest request) {
        return ReactiveBlocking.call(() -> ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseBuilder.buildSuccessResponse(null, messageService.addDetail(messageId, request))));
    }

    @PutMapping("/message-detail/update")
    @Operation(summary = "Update a message detail by ID")
    public Mono<ResponseEntity<GlobalResponse>> updateDetail(
            @RequestHeader UUID id,
            @Valid @RequestBody MessageDetailRequest request) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(
                GlobalResponseBuilder.buildSuccessResponse(null, messageService.updateDetail(id, request))));
    }

    @PostMapping("/bulk/send")
    @Operation(summary = "Send bulk SMS, email and/or notification")
    public Mono<ResponseEntity<GlobalResponse>> sendBulk(@Valid @RequestBody BulkSendRequest request) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(
                GlobalResponseBuilder.buildSuccessResponse(null, bulkSendService.sendBulk(request))));
    }
}
