package com.nepbrain.technology.auth.controller;

import com.nepbrain.technology.auth.payload.request.OAuthTokenSettingsRequest;
import com.nepbrain.technology.auth.payload.response.ServiceResponse;
import com.nepbrain.technology.auth.service.OAuthTokenSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Super Admin live update of OAuth access / refresh token TTLs for the registered Auth client.
 * Changes apply to newly issued tokens only.
 */
@RestController
@RequestMapping("/api/v2/auth/oauth-token-settings")
@RequiredArgsConstructor
public class OAuthTokenSettingsController {

    private final OAuthTokenSettingsService oAuthTokenSettingsService;

    @GetMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<ServiceResponse> get() {
        return ResponseEntity.ok().body(oAuthTokenSettingsService.getCurrent());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<ServiceResponse> update(@RequestBody OAuthTokenSettingsRequest request) {
        return ResponseEntity.ok().body(oAuthTokenSettingsService.update(request));
    }
}
