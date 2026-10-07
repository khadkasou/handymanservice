package com.nepbrain.technology.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth/social")
public class SocialAuthController {

    /**
     * Endpoint to initiate Google OAuth2 login
     * Redirects to: /oauth2/authorization/google
     */
    @GetMapping("/google")
    public ResponseEntity<Map<String, String>> googleLogin() {
        return ResponseEntity.ok(Map.of(
                "message", "Redirect to /oauth2/authorization/google to initiate Google login",
                "redirect_uri", "/oauth2/authorization/google"
        ));
    }

    /**
     * Endpoint to initiate Facebook OAuth2 login
     * Redirects to: /oauth2/authorization/facebook
     */
    @GetMapping("/facebook")
    public ResponseEntity<Map<String, String>> facebookLogin() {
        return ResponseEntity.ok(Map.of(
                "message", "Redirect to /oauth2/authorization/facebook to initiate Facebook login",
                "redirect_uri", "/oauth2/authorization/facebook"
        ));
    }
}

