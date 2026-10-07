package com.nepbrain.technology.communication.controller;

/*
 * @Created At 21/05/2024
 * @Author suroj.awal
 */

import com.nepbrain.technology.communication.annotation.CommunicationRestController;
import com.nepbrain.technology.communication.payload.request.MFARequest;
import com.nepbrain.technology.communication.payload.request.MFAVerifyRequest;
import com.nepbrain.technology.communication.service.MfaService;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CommunicationRestController
@RequiredArgsConstructor
public class MFAController {

    private final MfaService mfaService;

    @PostMapping("/generateMfaCode")
    public ResponseEntity<GlobalResponse> generateQrAndOtp(@RequestBody MFARequest mfaRequest) throws GlobalException {
        return ResponseEntity.ok(mfaService.generateQrAndOtp(mfaRequest));
    }

    @PostMapping("/generateMfaCode/micro")
    public ResponseEntity<GlobalResponse> generateQrAndMicroOtp(@RequestBody MFARequest mfaRequest) throws GlobalException {
        return ResponseEntity.ok(mfaService.generateQrAndOtp(mfaRequest));
    }

    @PostMapping("/verifyMfaCode")
    public ResponseEntity<GlobalResponse> verifyMfaCode(@RequestBody MFAVerifyRequest verifyRequest) {
        return ResponseEntity.ok(mfaService.verifyMFA(verifyRequest));
    }

    @PostMapping("/verifyMfaCode/micro")
    public ResponseEntity<GlobalResponse> verifyMicroMfaCode(@RequestBody MFAVerifyRequest verifyRequest) {
        return ResponseEntity.ok(mfaService.verifyMFA(verifyRequest));
    }
}
