package com.nepbrain.technology.communication.webclient;

/*
 * @Created At 05/06/2024
 * @Author suroj.awal
 */

import com.nepbrain.technology.communication.communication.InterCommunicationErrorHandler;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCommunication {

    @Value("${services.user}")
    private String userService;

    private final WebClient.Builder webclient;

    public GlobalResponse getUserDetail(List<UUID> userId) {
        return webclient.build()
                .post()
                .uri(userService + "detail/micro")
                .bodyValue(userId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, InterCommunicationErrorHandler::getMono)
                .bodyToMono(GlobalResponse.class)
                .block();
    }

}
