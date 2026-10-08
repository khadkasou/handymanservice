package com.nepbrain.technology.communication.webclient;

/*
 * @Created At 05/06/2024
 * @Author suroj.awal
 */

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.communication.communication.InterCommunicationErrorHandler;
import com.nepbrain.technology.communication.enums.ErrorCodeEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public List<UserContact> getUsersForBulkMessage(boolean fetchAll, List<String> roleIds, List<String> userIds) {
        if (fetchAll || (roleIds != null && !roleIds.isEmpty())) {
            throw new GlobalException(
                    ErrorCodeEnum.SRV001.name(),
                    "Select user ids. This stack has no user-service list for every user or a role.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        List<UUID> ids = new ArrayList<>();
        for (String userId : userIds) {
            ids.add(UUID.fromString(userId));
        }
        try {
            return contactsFrom(getUserDetail(ids));
        } catch (GlobalException ex) {
            throw ex;
        } catch (WebClientRequestException ex) {
            throw new GlobalException(
                    ErrorCodeEnum.SRV001.name(),
                    ErrorCodeEnum.SRV001.getValue(),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (IllegalArgumentException ex) {
            throw new GlobalException(ErrorCodeEnum.SRV001.name(), ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private List<UserContact> contactsFrom(GlobalResponse response) {
        if (response == null || response.getData() == null) {
            return List.of();
        }
        List<Map<String, Object>> rows = new ObjectMapper().convertValue(
                response.getData(), new TypeReference<List<Map<String, Object>>>() {});
        List<UserContact> contacts = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            String fullName = text(row.get("fullName"));
            if (fullName == null) {
                String first = text(row.get("firstName"));
                String last = text(row.get("lastName"));
                fullName = ((first == null ? "" : first) + " " + (last == null ? "" : last)).trim();
            }
            contacts.add(UserContact.builder()
                    .id(text(row.get("id")))
                    .email(text(row.get("email")))
                    .mobileNumber(firstText(row, "mobileNumber", "contactNumber", "mobile"))
                    .fullName(fullName.isEmpty() ? null : fullName)
                    .fcmTokens(tokens(row.get("fcmTokens")))
                    .build());
        }
        return contacts;
    }

    private String firstText(Map<String, Object> row, String... keys) {
        for (String key : keys) {
            String value = text(row.get(key));
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private String text(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    private List<String> tokens(Object value) {
        if (value == null) {
            return List.of();
        }
        return new ObjectMapper().convertValue(value, new TypeReference<List<String>>() {});
    }

}
