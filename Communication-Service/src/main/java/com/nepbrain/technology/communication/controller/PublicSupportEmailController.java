package com.nepbrain.technology.communication.controller;

import com.nepbrain.technology.communication.payload.request.SupportEmailRequest;
import com.nepbrain.technology.communication.service.SupportEmailService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public (unauthenticated) support email API for contact-us and similar flows.
 * Base path: /api/v2/communication/public
 */
@RestController
@RequestMapping("/api/v2/communication/public")
@RequiredArgsConstructor
@Tag(name = "Public Support Email", description = "Public support email API (no auth required)")
public class PublicSupportEmailController {

    private final SupportEmailService supportEmailService;

    @PostMapping("/support-email/create")
    @Operation(summary = "Create a support email (public)")
    public ResponseEntity<GlobalResponse> create(@Valid @RequestBody SupportEmailRequest request) {
        return ResponseEntity.ok().body(supportEmailService.create(request));
    }
}
