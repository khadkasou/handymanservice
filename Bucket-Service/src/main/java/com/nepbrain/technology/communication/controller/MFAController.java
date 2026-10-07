package com.nepbrain.technology.communication.controller;

import com.nepbrain.technology.communication.annotation.CommunicationRestController;
import com.nepbrain.technology.communication.payload.request.MFARequest;
import com.nepbrain.technology.communication.payload.request.MFAVerifyRequest;
import com.nepbrain.technology.communication.service.MfaService;
import com.nepbrain.technology.communication.util.ReactiveBlocking;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

@CommunicationRestController
@RequiredArgsConstructor
@Tag(name = "MFA", description = "MFA QR and TOTP APIs")
public class MFAController {

    private final MfaService mfaService;

    @PostMapping("/generateMfaCode")
    @Operation(summary = "Generate MFA QR and OTP")
    public Mono<ResponseEntity<GlobalResponse>> generateQrAndOtp(@RequestBody MFARequest mfaRequest) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(mfaService.generateQrAndOtp(mfaRequest)));
    }

    @PostMapping("/generateMfaCode/micro")
    @Operation(summary = "Generate MFA QR and OTP (microservice)")
    public Mono<ResponseEntity<GlobalResponse>> generateQrAndMicroOtp(@RequestBody MFARequest mfaRequest) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(mfaService.generateQrAndOtp(mfaRequest)));
    }

    @PostMapping("/verifyMfaCode")
    @Operation(summary = "Verify MFA code")
    public Mono<ResponseEntity<GlobalResponse>> verifyMfaCode(@RequestBody MFAVerifyRequest verifyRequest) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(mfaService.verifyMFA(verifyRequest)));
    }

    @PostMapping("/verifyMfaCode/micro")
    @Operation(summary = "Verify MFA code (microservice)")
    public Mono<ResponseEntity<GlobalResponse>> verifyMicroMfaCode(@RequestBody MFAVerifyRequest verifyRequest) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(mfaService.verifyMFA(verifyRequest)));
    }
}
