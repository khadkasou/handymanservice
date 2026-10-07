package com.nepbrain.technology.communication.grpc;

import com.nepbrain.technology.communication.enums.ChannelEnum;
import com.nepbrain.technology.communication.enums.LanguageEnum;
import com.nepbrain.technology.communication.enums.PlatformEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import com.nepbrain.technology.communication.payload.request.CommunicationRequest;
import com.nepbrain.technology.communication.service.CommunicationService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CommunicationTemplateGrpcService extends CommunicationTemplateServiceGrpc.CommunicationTemplateServiceImplBase {

    private final CommunicationService communicationService;

    @Override
    public void sendTemplateEmail(
            SendTemplateEmailRequest request,
            StreamObserver<SendTemplateEmailResponse> responseObserver
    ) {
        try {
            TopicEnum topic = parseTopic(request.getTopic());
            LanguageEnum language = parseLanguage(request.getLanguage());

            Map<String, String> payloadData = new HashMap<>(request.getDataMap());
            if (request.getName() != null && !request.getName().isBlank()) {
                payloadData.putIfAbsent("NAME", request.getName());
            }
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                payloadData.putIfAbsent("EMAIL", request.getEmail());
            }
            if (request.getUsername() != null && !request.getUsername().isBlank()) {
                payloadData.putIfAbsent("USERNAME", request.getUsername());
            }

            CommunicationRequest communicationRequest = CommunicationRequest.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .platform(PlatformEnum.WEB)
                    .topic(topic)
                    .language(language)
                    .sendTo(ChannelEnum.EMAIL)
                    .data(payloadData)
                    .build();

            communicationService.send(communicationRequest);
            responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Email sent successfully")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Failed to send template email via gRPC", ex);
            responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(ex.getMessage() == null ? "Failed to send email" : ex.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    private TopicEnum parseTopic(String topic) {
        if (topic == null || topic.isBlank()) {
            return TopicEnum.REGISTER;
        }
        try {
            return TopicEnum.valueOf(topic.trim().toUpperCase());
        } catch (Exception ex) {
            return TopicEnum.REGISTER;
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
}
