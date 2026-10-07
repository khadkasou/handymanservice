package com.nepbrain.technology.config.controller;
import com.nepbrain.technology.config.annotation.CustomConfigRestController;
import com.nepbrain.technology.config.service.ConfigService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;

@CustomConfigRestController @RequiredArgsConstructor
@Tag(name = "Config", description = "App configuration and dashboard APIs")
public class ConfigController {
    private final ConfigService configService;
    @GetMapping("/configurations") public ResponseEntity<GlobalResponse> configurations(
            @RequestParam(value = "is_authenticated", required = false, defaultValue = "0") int isAuthenticated) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CONFIGURATIONS", configService.configurations(isAuthenticated == 1)));
    }
    @GetMapping({"/dashboard-detail", "/dashboard-detail/"}) public ResponseEntity<GlobalResponse> dashboardDetail() {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("DASHBOARD_DETAIL", configService.dashboardDetail()));
    }
    @GetMapping("/firebase-detail") public ResponseEntity<GlobalResponse> firebaseDetail() {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("FIREBASE_DETAIL", configService.firebaseDetail()));
    }
}
