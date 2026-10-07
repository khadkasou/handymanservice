package com.nepbrain.technology.core.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.communication.enums.ErrorCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public class InterServiceCommunicationHandler {

    private InterServiceCommunicationHandler() {
    }

    private static final Logger logger = LoggerFactory.getLogger(InterServiceCommunicationHandler.class);

    public static Mono<Throwable> apply(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .flatMap(body -> {
                    try {
                        logger.error("Error Occur when calling inter service {}", body);
                        if (body.contains("LoadBalancer does not contain an instance")) {
                            return Mono.error(new GlobalException("SER001", HttpStatus.SERVICE_UNAVAILABLE));
                        }
                        GlobalResponse errorResponse = new ObjectMapper().readValue(body, GlobalResponse.class);
                        if (clientResponse.statusCode().equals(HttpStatusCode.valueOf(401))) {
                            return Mono.error(new GlobalException(errorResponse.getCode(), errorResponse.getMessage(),
                                    HttpStatus.UNAUTHORIZED));
                        } else if (clientResponse.statusCode().equals(HttpStatusCode.valueOf(403))) {
                            return Mono.error(new GlobalException(errorResponse.getCode(), errorResponse.getMessage(), HttpStatus.FORBIDDEN));
                        }
                        return Mono.error(new GlobalException(errorResponse.getCode(), errorResponse.getMessage(),HttpStatus.valueOf(clientResponse.statusCode().value())));
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        return Mono.error(e);
                    }
                });
    }

}
