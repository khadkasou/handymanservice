package com.nepbrain.technology.communication.service;

/*
 * @Created At 17/03/2024
 * @Author suroj.awal
 */

import com.nepbrain.technology.communication.entity.CompanySmsConfigEntity;
import com.nepbrain.technology.communication.enums.SmsProviderEnum;
import com.nepbrain.technology.communication.payload.request.BulkSMSRequest;
import com.nepbrain.technology.communication.payload.request.CommunicationRequest;
import com.nepbrain.technology.communication.payload.request.SmsRequest;
import com.nepbrain.technology.communication.payload.response.SmsResponse;
import com.nepbrain.technology.communication.record.SmsRecord;
import com.nepbrain.technology.core.exception.GlobalException;
import groovy.text.SimpleTemplateEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final WebClient.Builder webClient;
    private final CompanyMessagingChannelService companyMessagingChannelService;
    private static final SimpleTemplateEngine engine = new SimpleTemplateEngine();

    @Value("${sms.username}")
    private String username;

    @Value("${sms.password}")
    private String password;

    @Value("${sms.organizationCode}")
    private String organizationCode;

    public SmsResponse sendSMS(CommunicationRequest communicationRequest, SmsRecord smsRecord) throws GlobalException {
        try {
            String message = engine.createTemplate(smsRecord.body())
                    .make(communicationRequest.getData() != null ? communicationRequest.getData() : Map.of())
                    .toString();
            String toPhone = communicationRequest.getMobile() != null
                    ? communicationRequest.getMobile().toString()
                    : null;
            if (!StringUtils.hasText(toPhone)) {
                throw new GlobalException("SMS001", new IllegalArgumentException("Recipient mobile is required"));
            }

            Optional<CompanySmsConfigEntity> companySms =
                    companyMessagingChannelService.resolveCompanySmsChannel(communicationRequest.getCompanyId());
            if (companySms.isPresent()) {
                sendViaCompanyProvider(companySms.get(), toPhone, message);
                return SmsResponse.builder()
                        .responseCode(200)
                        .responseDescription("Sent via company SMS provider")
                        .smsText(message)
                        .build();
            }

            return sendViaPlatform(toPhone, message);
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            log.error("SMS send failed: {}", e.getMessage(), e);
            throw new GlobalException("SMS001", e);
        }
    }

    private void sendViaCompanyProvider(CompanySmsConfigEntity config, String toPhone, String message) {
        String authField = StringUtils.hasText(config.getAuthFieldName())
                ? config.getAuthFieldName().trim()
                : (config.getProvider() == SmsProviderEnum.AAKASH ? "auth_token" : "token");
        String apiUrl = config.getApiUrl().trim();

        // Prefer form-urlencoded (Sparrow / Aakash common style).
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add(authField, config.getToken());
        form.add("to", toPhone);
        form.add("text", message);
        if (StringUtils.hasText(config.getSmsFrom())) {
            form.add("from", config.getSmsFrom().trim());
        }

        String response = webClient.build().post()
                .uri(apiUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info("Company SMS ({}) sent to {} via {} — response={}",
                config.getProvider(), toPhone, apiUrl, response);
    }

    private SmsResponse sendViaPlatform(String toPhone, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username, password);
        headers.set("OrganisationCode", organizationCode);

        SmsRequest smsRequest = SmsRequest.builder()
                .isClientLogin("N")
                .receiverNo(toPhone)
                .message(message)
                .username(username)
                .password(password)
                .organisationCode(organizationCode)
                .build();
        return webClient.build().post()
                .uri("https://fastapi.jojolapatech.hrmsuite.com.np:8080/api/Sms/ExecuteSendSms")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue(smsRequest)
                .retrieve()
                .bodyToMono(SmsResponse.class)
                .block();
    }

    public SmsResponse sendBulkSMS(List<String> mobile, SmsRecord smsRecord) throws IOException, ClassNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username, password);
        headers.set("OrganisationCode", organizationCode);

        List<BulkSMSRequest> bulkSMSRequests = new ArrayList<>();
        for (String mb : mobile) {
            BulkSMSRequest bulkSMSRequest = BulkSMSRequest.builder()
                    .mobile(Long.valueOf(mb))
                    .message(engine.createTemplate(smsRecord.body()).toString())
                    .build();
            bulkSMSRequests.add(bulkSMSRequest);
        }
        SmsRequest smsRequest = SmsRequest.builder()
                .isClientLogin("N")
                .date(new Date())
                .batchId("b0")
                .smsDetails(bulkSMSRequests)
                .username(username)
                .password(password)
                .organisationCode(organizationCode)
                .build();

        return webClient.build().post()
                .uri("https://fastapi.jojolapatech.hrmsuite.com.np:8080/api/Sms/ExecuteSendSms")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue(smsRequest)
                .retrieve()
                .bodyToMono(SmsResponse.class)
                .block();
    }
}
