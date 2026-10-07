package com.nepbrain.technology.communication.communication;

import com.nepbrain.technology.communication.payload.request.AppMessageRequest;
import com.nepbrain.technology.communication.payload.request.CountryListRequest;
import com.nepbrain.technology.communication.payload.request.GeneralRequest;
import com.nepbrain.technology.communication.payload.response.SystemConfigResponse;
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
public class MasterServiceCommunication {

    private final WebClient.Builder webclient;

    @Value("${services.master}")
    private String master;

    public GlobalResponse getCategoryItemListByCategoryItemIds(GeneralRequest generalRequest) {
        return webclient.build()
                .post()
                .uri(master + "/validate/itemByCategory/micro")
                .bodyValue(generalRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, InterCommunicationErrorHandler::getMono)
                .bodyToMono(GlobalResponse.class)
                .block();
    }

    public GlobalResponse getItemDetailByIds(List<UUID> itemId) {
        return webclient.build()
                .post()
                .uri(master + "find-list-of-category-items/micro")
                .bodyValue(itemId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, InterCommunicationErrorHandler::getMono)
                .bodyToMono(GlobalResponse.class)
                .block();
    }

    public GlobalResponse countryDetail(UUID countryId) {
        return webclient.build()
                .get()
                .uri(master + "country/micro/" + countryId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, InterCommunicationErrorHandler::getMono)
                .bodyToMono(GlobalResponse.class)
                .block();
    }

    public GlobalResponse getGender(List<GeneralRequest> request) {
        return webclient.build()
                .post()
                .uri(master + "validate/item/ByCategory/micro")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, InterCommunicationErrorHandler::getMono)
                .bodyToMono(GlobalResponse.class)
                .block();
    }

    public GlobalResponse getGender(GeneralRequest request) {
        return webclient.build()
                .post()
                .uri(master + "validate/itemByCategory/micro")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, InterCommunicationErrorHandler::getMono)
                .bodyToMono(GlobalResponse.class)
                .block();
    }

    public GlobalResponse getCountriesByIds(CountryListRequest countryIds) {
            return webclient.build()
                    .post()
                    .uri(master + "country/validate/micro")
                    .bodyValue(countryIds)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, InterCommunicationErrorHandler::getMono)
                    .bodyToMono(GlobalResponse.class)
                    .block();
    }


    public SystemConfigResponse getSystemConfig() {
        return webclient.build()
                .get()
                .uri(master + "system/config/detail/micro")
                .retrieve()
                .onStatus(HttpStatusCode::isError, InterCommunicationErrorHandler::getMono)
                .bodyToMono(SystemConfigResponse.class)
                .block();
    }

    public GlobalResponse getMessage(AppMessageRequest request) {
        return webclient.build()
                .post()
                .uri(master + "/application/message")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, InterCommunicationErrorHandler::getMono)
                .bodyToMono(GlobalResponse.class)
                .block();
    }


}
