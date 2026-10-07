package com.nepbrain.technology.bucket.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
public class InterCommunicationErrorHandler {

    private InterCommunicationErrorHandler() {
    }


    public static Mono<GlobalException> apply(ClientResponse clientResponse) {
        return getMono(clientResponse);
    }

    @NotNull
    private static Mono<GlobalException> getMono(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .flatMap(body -> {
                    try {
                        GlobalResponse errorResponse = new ObjectMapper().readValue(body, GlobalResponse.class);
                        if (clientResponse.statusCode().equals(HttpStatusCode.valueOf(401))) {
                            return Mono.error(new GlobalException("IAM401", "Unauthorized access!", HttpStatus.UNAUTHORIZED));
                        }
                        if (clientResponse.statusCode().equals(HttpStatusCode.valueOf(403))) {
                            return Mono.error(new GlobalException("IAM403", "Access Denied. Provide correct username and password", HttpStatus.FORBIDDEN));
                        }
                        log.error(new Gson().toJson(errorResponse));
                        return Mono.error(new GlobalException(errorResponse.getMessage(), (HttpStatus) clientResponse.statusCode()));
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        return Mono.error(new GlobalException("Code not Registered."));
                    }
                });
    }

}
