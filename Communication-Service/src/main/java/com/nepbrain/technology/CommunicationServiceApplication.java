package com.nepbrain.technology;

import net.devh.boot.grpc.server.autoconfigure.GrpcMetadataConsulConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(
        exclude = GrpcMetadataConsulConfiguration.class,
        excludeName = "org.springframework.boot.security.autoconfigure.web.reactive.ReactiveWebSecurityAutoConfiguration"
)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class CommunicationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunicationServiceApplication.class, args);
    }


}
