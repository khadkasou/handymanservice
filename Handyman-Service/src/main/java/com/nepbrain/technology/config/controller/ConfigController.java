package com.nepbrain.technology.config.controller;

import com.nepbrain.technology.config.annotation.CustomConfigRestController;
import com.nepbrain.technology.config.service.ConfigService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CustomConfigRestController
@RequiredArgsConstructor
@Tag(name = "Config")
public class ConfigController {

    private final ConfigService configService;

    @GetMapping("/configurations")
    @Operation(summary = "App configurations")
    public ResponseEntity<GlobalResponse> configurations(@RequestParam(value = "is_authenticated", required = false, defaultValue = "0") int isAuthenticated) {
        return ResponseEntity.ok().body(configService.configurations(isAuthenticated == 1));
    }

    @GetMapping({"/dashboard-detail", "/dashboard-detail/"})
    @Operation(summary = "Dashboard detail")
    public ResponseEntity<GlobalResponse> dashboardDetail() {
        return ResponseEntity.ok().body(configService.dashboardDetail());
    }

    @GetMapping("/firebase-detail")
    @Operation(summary = "Firebase detail")
    public ResponseEntity<GlobalResponse> firebaseDetail() {
        return ResponseEntity.ok().body(configService.firebaseDetail());
    }
}
