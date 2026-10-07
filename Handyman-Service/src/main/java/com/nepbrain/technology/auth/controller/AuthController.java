package com.nepbrain.technology.auth.controller;
import com.nepbrain.technology.auth.annotation.CustomAuthRestController;
import com.nepbrain.technology.auth.payload.request.*;
import com.nepbrain.technology.auth.service.AuthService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@CustomAuthRestController @RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication APIs")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register") @Operation(summary = "Register")
    public ResponseEntity<GlobalResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseBuilder.buildSuccessResponse("REGISTER", authService.register(request)));
    }
    @PostMapping("/login") @Operation(summary = "Login")
    public ResponseEntity<GlobalResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("LOGIN", authService.login(request)));
    }
    @PostMapping("/social-login") @Operation(summary = "Social login")
    public ResponseEntity<GlobalResponse> socialLogin(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("SOCIAL_LOGIN", authService.socialLogin(request)));
    }
    @PostMapping("/update-profile") @Operation(summary = "Update profile")
    public ResponseEntity<GlobalResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("UPDATE_PROFILE", authService.updateProfile(request)));
    }
    @PostMapping("/change-password") @Operation(summary = "Change password")
    public ResponseEntity<GlobalResponse> changePassword(@RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("CHANGE_PASSWORD", "Password changed successfully"));
    }
    @PostMapping("/forgot-password") @Operation(summary = "Forgot password")
    public ResponseEntity<GlobalResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("FORGOT_PASSWORD", "Password reset email sent"));
    }
    @PostMapping("/delete-user-account") @Operation(summary = "Delete account")
    public ResponseEntity<GlobalResponse> deleteUserAccount(@RequestParam(required = false) UUID id,
            @RequestHeader(value = "id", required = false) UUID headerId) {
        authService.deleteUserAccount(id != null ? id : headerId);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("DELETE_ACCOUNT", "Account deleted"));
    }
    @PostMapping("/user-email-verify") @Operation(summary = "Verify email")
    public ResponseEntity<GlobalResponse> verifyEmail(@RequestBody EmailVerifyRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("EMAIL_VERIFY", "Email verified"));
    }
    @GetMapping("/logout") @Operation(summary = "Logout")
    public ResponseEntity<GlobalResponse> logout(@RequestParam(required = false) UUID id,
            @RequestHeader(value = "id", required = false) UUID headerId) {
        if (id != null || headerId != null) authService.logout(id != null ? id : headerId);
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("LOGOUT", "Logged out"));
    }
    @GetMapping("/user-detail") @Operation(summary = "User detail")
    public ResponseEntity<GlobalResponse> userDetail(@RequestParam UUID id) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("USER_DETAIL", authService.getUserDetail(id)));
    }
}
