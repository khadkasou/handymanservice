package com.nepbrain.technology.communication.config;

import com.nepbrain.technology.bucket.grpc.BucketGrpcServiceGrpc;
import com.nepbrain.technology.communication.grpc.CommunicationTemplateServiceGrpc;
import com.nepbrain.technology.communication.grpc.OtpServiceGrpc;
import io.grpc.BindableService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Logs which gRPC services are registered on startup (bucket upload + communication templates).
 */
@Slf4j
@Configuration
public class GrpcCommunicationServerConfig {

    @Value("${grpc.server.port:8906}")
    private int grpcPort;

    @Autowired(required = false)
    private List<BindableService> grpcServices;

    @PostConstruct
    void logGrpcServices() {
        if (grpcServices == null || grpcServices.isEmpty()) {
            log.warn("No gRPC services registered on port {} — rebuild Bucket-Service image with communication module",
                    grpcPort);
            return;
        }
        String names = grpcServices.stream()
                .map(s -> s.bindService().getServiceDescriptor().getName())
                .collect(Collectors.joining(", "));
        log.info("gRPC server on port {} exposes: {}", grpcPort, names);

        boolean hasTemplate = grpcServices.stream()
                .anyMatch(s -> CommunicationTemplateServiceGrpc.SERVICE_NAME.equals(
                        s.bindService().getServiceDescriptor().getName()));
        boolean hasBucket = grpcServices.stream()
                .anyMatch(s -> BucketGrpcServiceGrpc.SERVICE_NAME.equals(
                        s.bindService().getServiceDescriptor().getName()));
        boolean hasOtp = grpcServices.stream()
                .anyMatch(s -> OtpServiceGrpc.SERVICE_NAME.equals(
                        s.bindService().getServiceDescriptor().getName()));

        if (!hasTemplate) {
            log.error("Missing {} — User-Service welcome emails will fail with UNIMPLEMENTED",
                    CommunicationTemplateServiceGrpc.SERVICE_NAME);
        }
        if (hasBucket) {
            log.info("Bucket upload gRPC ({}) is active", BucketGrpcServiceGrpc.SERVICE_NAME);
        }
        if (hasOtp) {
            log.info("OTP gRPC ({}) is active", OtpServiceGrpc.SERVICE_NAME);
        }
    }
}
