package com.nepbrain.technology.helpdesk.controller;

import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.helpdesk.annotation.CustomHelpdeskRestController;
import com.nepbrain.technology.helpdesk.payload.request.HelpdeskActivityRequest;
import com.nepbrain.technology.helpdesk.payload.request.HelpdeskTicketRequest;
import com.nepbrain.technology.helpdesk.service.HelpdeskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@CustomHelpdeskRestController
@RequiredArgsConstructor
@Tag(name = "Helpdesk")
public class HelpdeskController {

    private final HelpdeskService helpdeskService;

    @PostMapping("/helpdesk-save")
    @Operation(summary = "Save helpdesk ticket")
    public ResponseEntity<GlobalResponse> helpdeskSave(@RequestBody HelpdeskTicketRequest request) {
        return ResponseEntity.ok().body(helpdeskService.helpdeskSave(request));
    }

    @GetMapping("/helpdesk-list")
    @Operation(summary = "Helpdesk ticket list")
    public ResponseEntity<GlobalResponse> helpdeskList(@RequestParam(required = false) String status,
                                                       @RequestParam(value = "customer_id", required = false) UUID customerId) {
        return ResponseEntity.ok().body(helpdeskService.helpdeskList(status, customerId));
    }

    @GetMapping("/helpdesk-detail")
    @Operation(summary = "Helpdesk ticket detail")
    public ResponseEntity<GlobalResponse> helpdeskDetail(@RequestParam UUID id) {
        return ResponseEntity.ok().body(helpdeskService.helpdeskDetail(id));
    }

    @PostMapping("/helpdesk-activity-save/{helpDeskId}")
    @Operation(summary = "Save helpdesk activity")
    public ResponseEntity<GlobalResponse> helpdeskActivitySave(@PathVariable UUID helpDeskId,
                                                               @RequestBody HelpdeskActivityRequest request) {
        return ResponseEntity.ok().body(helpdeskService.helpdeskActivitySave(helpDeskId, request));
    }

    @PostMapping("/helpdesk-closed/{helpDeskId}")
    @Operation(summary = "Close helpdesk ticket")
    public ResponseEntity<GlobalResponse> helpdeskClosed(@PathVariable UUID helpDeskId) {
        return ResponseEntity.ok().body(helpdeskService.helpdeskClosed(helpDeskId));
    }
}
