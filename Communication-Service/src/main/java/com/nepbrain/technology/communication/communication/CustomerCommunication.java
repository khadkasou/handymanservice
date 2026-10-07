package com.nepbrain.technology.communication.communication;

/*
 * @Created At 14/06/2024
 * @Author suroj.awal
 */

import com.nepbrain.technology.communication.payload.request.CustomerIdRequest;
import com.nepbrain.technology.communication.payload.request.SpecifiedCustomerRequest;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class CustomerCommunication {

    private static final Logger logger = LoggerFactory.getLogger(CustomerCommunication.class);

    private final WebClient.Builder webclient;

    @Value("${services.customer}")
    private String customerURL;

    public GlobalResponse getMobileNumbersFromCodes(CustomerIdRequest customer) {
        return webclient.build()
                .post()
                .uri(customerURL + "mobile-by-codes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customer)
                .retrieve()
                .onStatus(HttpStatusCode::isError, InterCommunicationErrorHandler::getMono)
                .bodyToMono(GlobalResponse.class)
                .block();
    }

    public GlobalResponse getAllCustomer() {
        return webclient.build()
                .get()
                .uri(customerURL + "mobile-for-all")
                .retrieve()
                .onStatus(HttpStatusCode::isError, InterCommunicationErrorHandler::getMono)
                .bodyToMono(GlobalResponse.class)
                .block();
    }

    public GlobalResponse getSpecifiedCustomer(SpecifiedCustomerRequest customer) {
        return webclient.build()
                .post()
                .uri(customerURL + "mobile-by-gender-and-nationality")
                .bodyValue(customer)
                .retrieve()
                .onStatus(HttpStatusCode::isError, InterCommunicationErrorHandler::getMono)
                .bodyToMono(GlobalResponse.class)
                .block();
    }

}
