package com.nepbrain.technology.communication.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
@Service
public class InterCommunicationErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(InterCommunicationErrorHandler.class);

    private InterCommunicationErrorHandler() {

    }
    @NotNull
    public static Mono<Throwable> getMono(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .flatMap(body -> {
                    try {
                        GlobalResponse errorResponse = new ObjectMapper().readValue(body, GlobalResponse.class);
                        InterCommunicationErrorHandler.logger.error(new Gson().toJson(errorResponse));
                        return Mono.error(new GlobalException(errorResponse.getCode(), errorResponse.getMessage(), HttpStatus.valueOf(clientResponse.statusCode().value())));
                    } catch (IOException e) {
                        InterCommunicationErrorHandler.logger.error(e.getMessage());
                        return Mono.error(new GlobalException("COMM001", e));
                    }
                });
    }

}
