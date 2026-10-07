package com.nepbrain.technology.helpdesk.controller;
import com.nepbrain.technology.helpdesk.annotation.CustomHelpdeskRestController;
import com.nepbrain.technology.helpdesk.payload.request.*;
import com.nepbrain.technology.helpdesk.service.HelpdeskService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@CustomHelpdeskRestController @RequiredArgsConstructor
@Tag(name = "Helpdesk", description = "Helpdesk ticket APIs")
public class HelpdeskController {
    private final HelpdeskService helpdeskService;
    @PostMapping("/helpdesk-save") public ResponseEntity<GlobalResponse> helpdeskSave(@RequestBody HelpdeskTicketRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("HELPDESK_SAVE", helpdeskService.helpdeskSave(request)));
    }
    @GetMapping("/helpdesk-list") public ResponseEntity<GlobalResponse> helpdeskList(@RequestParam(required = false) String status,
            @RequestParam(value = "customer_id", required = false) UUID customerId) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("HELPDESK_LIST", helpdeskService.helpdeskList(status, customerId)));
    }
    @GetMapping("/helpdesk-detail") public ResponseEntity<GlobalResponse> helpdeskDetail(@RequestParam UUID id) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("HELPDESK_DETAIL", helpdeskService.helpdeskDetail(id)));
    }
    @PostMapping("/helpdesk-activity-save/{helpDeskId}") public ResponseEntity<GlobalResponse> helpdeskActivitySave(@PathVariable UUID helpDeskId, @RequestBody HelpdeskActivityRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("HELPDESK_ACTIVITY_SAVE", helpdeskService.helpdeskActivitySave(helpDeskId, request)));
    }
    @PostMapping("/helpdesk-closed/{helpDeskId}") public ResponseEntity<GlobalResponse> helpdeskClosed(@PathVariable UUID helpDeskId) {
        helpdeskService.helpdeskClosed(helpDeskId);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("HELPDESK_CLOSED", "Ticket closed"));
    }
}
