package com.nepbrain.technology.communication.controller;

import com.nepbrain.technology.communication.annotation.CommunicationRestController;
import com.nepbrain.technology.communication.enums.MessageModuleEnum;
import com.nepbrain.technology.communication.enums.PlatformEnum;
import com.nepbrain.technology.communication.payload.request.CommunicationRequest;
import com.nepbrain.technology.communication.payload.request.CommunicationSearchFilterPaginationRequest;
import com.nepbrain.technology.communication.payload.request.OTPVerifyRequest;
import com.nepbrain.technology.communication.service.CommunicationService;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CommunicationRestController
@RequiredArgsConstructor
public class CommunicationController {

    private final CommunicationService communicationService;

    @PostMapping("/send")
    public ResponseEntity<GlobalResponse> sendMessage(
            @RequestHeader(name = "User-Id", required = false) String userId,
            @RequestHeader(name = "Device-Id", required = false) String deviceId,
            @RequestHeader(name = "Device-Type", required = false) String deviceType,
            @RequestHeader(name = "Device-Version", required = false) String deviceVersion,
            @RequestHeader(name = "Channel-Id", required = false) String channelId,
            @RequestBody CommunicationRequest request) throws GlobalException {
        communicationService.send(request);
        return ResponseEntity.ok()
                .body(GlobalResponseBuilder.buildSuccessResponse("Message sent successfully."));
    }

    @PostMapping("/send/micro")
    public ResponseEntity<GlobalResponse> sendMicroMessage(
            @RequestHeader(name = "User-Id", required = false) String userId,
            @RequestHeader(name = "Device-Id", required = false) String deviceId,
            @RequestHeader(name = "Device-Type", required = false) String deviceType,
            @RequestHeader(name = "Device-Version", required = false) String deviceVersion,
            @RequestHeader(name = "Channel-Id", required = false) String channelId,
            @RequestBody CommunicationRequest request) throws GlobalException {
        request.setPlatform(PlatformEnum.WEB);
        communicationService.send(request);
        return ResponseEntity.ok()
                .body(GlobalResponseBuilder.buildSuccessResponse("Message sent successfully."));
    }

    @PostMapping("/generateOTP")
    public ResponseEntity<GlobalResponse> generateOTP(
            @RequestHeader(name = "User-Id", required = false) String userId,
            @RequestHeader(name = "Device-Id", required = false) String deviceId,
            @RequestHeader(name = "Device-Type", required = false) String deviceType,
            @RequestHeader(name = "Device-Version", required = false) String deviceVersion,
            @RequestHeader(name = "Channel-Id", required = false) String channelId,
            @RequestBody CommunicationRequest request) throws GlobalException {
        return ResponseEntity.ok()
                .body(communicationService.generateOTP(request));
    }

    @PostMapping("/generateOTP/micro")
    public ResponseEntity<GlobalResponse> generateMicroOTP(
            @RequestHeader(name = "User-Id", required = false) String userId,
            @RequestHeader(name = "Device-Id", required = false) String deviceId,
            @RequestHeader(name = "Device-Type", required = false) String deviceType,
            @RequestHeader(name = "Device-Version", required = false) String deviceVersion,
            @RequestHeader(name = "Channel-Id", required = false) String channelId,
            @RequestBody CommunicationRequest request) throws GlobalException {
        return ResponseEntity.ok()
                .body(communicationService.generateOTP(request));
    }

    @PostMapping("/verifyOTP")
    public ResponseEntity<GlobalResponse> verifyOTP(
            @RequestHeader(name = "User-Id", required = false) String userId,
            @RequestHeader(name = "Device-Id", required = false) String deviceId,
            @RequestHeader(name = "Device-Type", required = false) String deviceType,
            @RequestHeader(name = "Device-Version", required = false) String deviceVersion,
            @RequestHeader(name = "Channel-Id", required = false) String channelId,
            @RequestBody OTPVerifyRequest request) throws GlobalException {
        return ResponseEntity.ok()
                .body(communicationService.verifyOTP(request));
    }

    @PostMapping("/logs")
    public ResponseEntity<GlobalResponse> getCommunicationsLog(@RequestBody CommunicationSearchFilterPaginationRequest request) {
        return ResponseEntity.ok(communicationService.getCommunicationsLog(request));
    }

    @GetMapping("/topic")
    public ResponseEntity<GlobalResponse> getTopicEnums(
            @RequestParam(name = "module", required = false) MessageModuleEnum module) {
        return ResponseEntity.ok(communicationService.getTopicEnums(module));
    }

}
