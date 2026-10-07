package com.nepbrain.technology.communication.controller;

import com.nepbrain.technology.communication.annotation.CommunicationRestController;
import com.nepbrain.technology.communication.payload.request.CompanyMessagingConfigRequest;
import com.nepbrain.technology.communication.payload.request.TestEmailRequest;
import com.nepbrain.technology.communication.payload.request.TestSmsRequest;
import com.nepbrain.technology.communication.service.CompanyMessagingConfigService;
import com.nepbrain.technology.communication.util.ReactiveBlocking;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CommunicationRestController
@RequiredArgsConstructor
@Tag(name = "Company Messaging Config", description = "Per-company email / SMS settings (Bucket-Service)")
public class CompanyMessagingConfigController {

    private final CompanyMessagingConfigService companyMessagingConfigService;

    @GetMapping("/messaging-config/get-by-company-id")
    @Operation(summary = "Get messaging config for company (empty defaults if none saved)")
    public Mono<ResponseEntity<GlobalResponse>> getByCompanyId(@RequestHeader UUID companyId) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse(
                "MESSAGING_CONFIG_RETRIEVED",
                companyMessagingConfigService.getByCompanyId(requireCompanyId(companyId)))));
    }

    @PostMapping("/messaging-config/save")
    @Operation(summary = "Create or update email config + SMS provider rows")
    public Mono<ResponseEntity<GlobalResponse>> save(
            @RequestHeader UUID companyId,
            @RequestBody CompanyMessagingConfigRequest request) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse(
                "MESSAGING_CONFIG_SAVED",
                companyMessagingConfigService.save(requireCompanyId(companyId), request))));
    }

    @PostMapping("/messaging-config/test-email")
    @Operation(summary = "Validate email/SMTP settings")
    public Mono<ResponseEntity<GlobalResponse>> testEmail(
            @RequestHeader UUID companyId,
            @Valid @RequestBody TestEmailRequest request) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse(
                "MESSAGING_CONFIG_TEST_EMAIL",
                companyMessagingConfigService.testEmail(requireCompanyId(companyId), request))));
    }

    @PostMapping("/messaging-config/test-sms")
    @Operation(summary = "Validate SMS provider settings")
    public Mono<ResponseEntity<GlobalResponse>> testSms(
            @RequestHeader UUID companyId,
            @Valid @RequestBody TestSmsRequest request) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse(
                "MESSAGING_CONFIG_TEST_SMS",
                companyMessagingConfigService.testSms(requireCompanyId(companyId), request))));
    }

    private static UUID requireCompanyId(UUID companyId) {
        if (companyId == null) {
            throw new GlobalException("COMPANY_REQUIRED", HttpStatus.BAD_REQUEST);
        }
        return companyId;
    }
}
