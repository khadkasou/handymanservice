package com.nepbrain.technology.communication.controller;

import com.nepbrain.technology.communication.annotation.CommunicationRestController;
import com.nepbrain.technology.communication.enums.StatusEnum;
import com.nepbrain.technology.communication.payload.request.BulkSendRequest;
import com.nepbrain.technology.communication.payload.request.MessageDataRequest;
import com.nepbrain.technology.communication.payload.request.MessageDetailRequest;
import com.nepbrain.technology.communication.payload.request.MessageRequest;
import com.nepbrain.technology.communication.service.BulkSendService;
import com.nepbrain.technology.communication.service.MessageService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/*
 * @Created At 03/03/2024
 * @Author suroj.awal
 */
@CommunicationRestController
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message and message detail CRUD APIs")
public class MessageController {

    private final MessageService messageService;
    private final BulkSendService bulkSendService;

    @PostMapping("/message/create")
    @Operation(summary = "Create a new message with details")
    public ResponseEntity<GlobalResponse> create(@Valid @RequestBody MessageRequest request) {
        return ResponseEntity.ok().body(messageService.create(request));
    }

    @PutMapping("/message/update")
    @Operation(summary = "Update an existing message")
    public ResponseEntity<GlobalResponse> update(@RequestHeader UUID id, @Valid @RequestBody MessageRequest request) {
        request.setId(id);
        return ResponseEntity.ok().body(messageService.update(request));
    }

    @GetMapping("/message/detail")
    @Operation(summary = "Get message by ID")
    public ResponseEntity<GlobalResponse> getById(@RequestHeader UUID id) {
        return ResponseEntity.ok().body(messageService.getById(id));
    }

    @PostMapping("/message/list")
    @Operation(summary = "List messages with pagination and filters")
    public ResponseEntity<GlobalResponse> getAll(@Valid @RequestBody MessageDataRequest request) {
        return ResponseEntity.ok().body(messageService.getAll(request));
    }

    @DeleteMapping("/message/delete")
    @Operation(summary = "Delete message by ID")
    public ResponseEntity<GlobalResponse> delete(@RequestHeader UUID id) {
        return ResponseEntity.ok().body(messageService.delete(id));
    }

    @PutMapping("/message/change-status")
    @Operation(summary = "Change message status")
    public ResponseEntity<GlobalResponse> changeStatus(@RequestHeader UUID id, @RequestHeader StatusEnum status) {
        return ResponseEntity.ok().body(messageService.changeStatus(id, status));
    }

    @PostMapping("/message-detail/create")
    @Operation(summary = "Add or update a message detail by language (adds if language missing, updates if present; only non-null fields are applied)")
    public ResponseEntity<GlobalResponse> addDetail(@RequestHeader UUID messageId, @Valid @RequestBody MessageDetailRequest request) {
        return ResponseEntity.ok().body(messageService.addDetail(messageId, request));
    }

    @PutMapping("/message-detail/update")
    @Operation(summary = "Update a message detail by ID")
    public ResponseEntity<GlobalResponse> updateDetail(@RequestHeader UUID id, @Valid @RequestBody MessageDetailRequest request) {
        return ResponseEntity.ok().body(messageService.updateDetail(id, request));
    }

    @PostMapping("/bulk/send")
    @Operation(summary = "Send bulk SMS, email and/or notification to all users, by role, or selected users (uses User-Service gRPC)")
    public ResponseEntity<GlobalResponse> sendBulk(@Valid @RequestBody BulkSendRequest request) {
        return ResponseEntity.ok().body(bulkSendService.sendBulk(request));
    }
}
