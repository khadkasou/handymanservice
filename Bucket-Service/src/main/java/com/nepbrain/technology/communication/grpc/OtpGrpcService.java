package com.nepbrain.technology.communication.grpc;

import com.nepbrain.technology.communication.enums.ChannelEnum;
import com.nepbrain.technology.communication.enums.LanguageEnum;
import com.nepbrain.technology.communication.enums.PlatformEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import com.nepbrain.technology.communication.payload.request.CommunicationRequest;
import com.nepbrain.technology.communication.payload.request.OTPVerifyRequest;
import com.nepbrain.technology.communication.service.CommunicationService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class OtpGrpcService extends OtpServiceGrpc.OtpServiceImplBase {

    private final CommunicationService communicationService;

    @Override
    public void generateOtp(GenerateOtpRequest request, StreamObserver<OtpResponse> responseObserver) {
        try {
            TopicEnum topic = parseTopic(request.getTopic());
            LanguageEnum language = parseLanguage(request.getLanguage());

            CommunicationRequest.CommunicationRequestBuilder builder = CommunicationRequest.builder()
                    .username(request.getEmail())
                    .email(request.getEmail())
                    .platform(PlatformEnum.WEB)
                    .topic(topic)
                    .language(language)
                    .sendTo(ChannelEnum.EMAIL);

            if (request.getMobile() != null && !request.getMobile().isBlank()) {
                Long mobile = parseMobile(request.getMobile());
                if (mobile != null) {
                    builder.mobile(mobile);
                }
            }
            if (request.getFcmToken() != null && !request.getFcmToken().isBlank()) {
                builder.tokens(List.of(request.getFcmToken().trim()));
            }

            CommunicationRequest communicationRequest = builder.build();
            GlobalResponse response = communicationService.generateOTP(communicationRequest);
            boolean success = response != null && "SUCCESS".equalsIgnoreCase(String.valueOf(response.getStatus()));
            String message = response != null && response.getMessage() != null
                    ? response.getMessage()
                    : "OTP generation completed";

            responseObserver.onNext(
                    OtpResponse.newBuilder()
                            .setSuccess(success)
                            .setMessage(message)
                            .build()
            );
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Failed to generate OTP via gRPC", ex);
            responseObserver.onNext(
                    OtpResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage(ex.getMessage() == null ? "Failed to generate OTP" : ex.getMessage())
                            .build()
            );
            responseObserver.onCompleted();
        }
    }

    @Override
    public void verifyOtp(VerifyOtpRequest request, StreamObserver<OtpResponse> responseObserver) {
        try {
            TopicEnum topic = parseTopic(request.getTopic());

            OTPVerifyRequest verifyRequest = new OTPVerifyRequest();
            verifyRequest.setUsername(request.getEmail());
            verifyRequest.setCode(request.getCode());
            verifyRequest.setTopic(topic);

            GlobalResponse response = communicationService.verifyOTP(verifyRequest);
            boolean success = response != null && "SUCCESS".equalsIgnoreCase(String.valueOf(response.getStatus()));
            String message = response != null && response.getMessage() != null
                    ? response.getMessage()
                    : "OTP verification completed";

            responseObserver.onNext(
                    OtpResponse.newBuilder()
                            .setSuccess(success)
                            .setMessage(message)
                            .build()
            );
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Failed to verify OTP via gRPC", ex);
            responseObserver.onNext(
                    OtpResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage(ex.getMessage() == null ? "Failed to verify OTP" : ex.getMessage())
                            .build()
            );
            responseObserver.onCompleted();
        }
    }

    private TopicEnum parseTopic(String topic) {
        if (topic == null || topic.isBlank()) {
            return TopicEnum.FORGOT_PASSWORD;
        }
        try {
            return TopicEnum.valueOf(topic.trim().toUpperCase());
        } catch (Exception ex) {
            return TopicEnum.FORGOT_PASSWORD;
        }
    }

    private LanguageEnum parseLanguage(String language) {
        if (language == null || language.isBlank()) {
            return LanguageEnum.EN;
        }
        try {
            return LanguageEnum.valueOf(language.trim().toUpperCase());
        } catch (Exception ex) {
            return LanguageEnum.EN;
        }
    }

    private Long parseMobile(String mobile) {
        if (mobile == null || mobile.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(mobile.trim().replaceAll("[^0-9]", ""));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}

