package com.nepbrain.technology.communication.controller;

import com.nepbrain.technology.communication.annotation.CommunicationRestController;
import com.nepbrain.technology.communication.service.CommunicationDashboardAnalyticsService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@CommunicationRestController
@RequiredArgsConstructor
@Tag(name = "Communication Dashboard", description = "Platform communication analytics")
public class CommunicationDashboardController {

    private final CommunicationDashboardAnalyticsService analyticsService;

    @GetMapping("/dashboard/analytics")
    @Operation(summary = "Support message and notice analytics for platform dashboard")
    public ResponseEntity<GlobalResponse> getAnalytics() {
        return ResponseEntity.ok().body(analyticsService.getAnalytics());
    }
}
