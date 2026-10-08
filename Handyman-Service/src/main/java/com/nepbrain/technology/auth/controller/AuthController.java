package com.nepbrain.technology.auth.controller;

import com.nepbrain.technology.auth.annotation.CustomAuthRestController;
import com.nepbrain.technology.auth.payload.request.ChangePasswordRequest;
import com.nepbrain.technology.auth.payload.request.EmailVerifyRequest;
import com.nepbrain.technology.auth.payload.request.ForgotPasswordRequest;
import com.nepbrain.technology.auth.payload.request.LoginRequest;
import com.nepbrain.technology.auth.payload.request.RegisterRequest;
import com.nepbrain.technology.auth.payload.request.UpdateProfileRequest;
import com.nepbrain.technology.auth.service.AuthService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@CustomAuthRestController
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register")
    public ResponseEntity<GlobalResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok().body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login")
    public ResponseEntity<GlobalResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok().body(authService.login(request));
    }

    @PostMapping("/social-login")
    @Operation(summary = "Social login")
    public ResponseEntity<GlobalResponse> socialLogin(@RequestBody LoginRequest request) {
        return ResponseEntity.ok().body(authService.socialLogin(request));
    }

    @PostMapping("/update-profile")
    @Operation(summary = "Update profile")
    public ResponseEntity<GlobalResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok().body(authService.updateProfile(request));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password")
    public ResponseEntity<GlobalResponse> changePassword(@RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok().body(authService.changePassword(request));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password")
    public ResponseEntity<GlobalResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok().body(authService.forgotPassword(request));
    }

    @PostMapping("/delete-user-account")
    @Operation(summary = "Delete account")
    public ResponseEntity<GlobalResponse> deleteUserAccount(@RequestParam(required = false) UUID id,
                                                            @RequestHeader(value = "id", required = false) UUID headerId) {
        return ResponseEntity.ok().body(authService.deleteUserAccount(id != null ? id : headerId));
    }

    @PostMapping("/user-email-verify")
    @Operation(summary = "Verify email")
    public ResponseEntity<GlobalResponse> verifyEmail(@RequestBody EmailVerifyRequest request) {
        return ResponseEntity.ok().body(authService.verifyEmail(request));
    }

    @GetMapping("/logout")
    @Operation(summary = "Logout")
    public ResponseEntity<GlobalResponse> logout(@RequestParam(required = false) UUID id,
                                                 @RequestHeader(value = "id", required = false) UUID headerId) {
        if (id == null && headerId == null) {
            return ResponseEntity.ok().body(GlobalResponseBuilder.buildSuccessResponse("message.auth.logout.success"));
        }
        return ResponseEntity.ok().body(authService.logout(id != null ? id : headerId));
    }

    @GetMapping("/user-detail")
    @Operation(summary = "User detail")
    public ResponseEntity<GlobalResponse> userDetail(@RequestParam UUID id) {
        return ResponseEntity.ok().body(authService.getUserDetail(id));
    }
}
