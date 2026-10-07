package com.nepbrain.technology.communication.service;

/*
 * @Created At 17/03/2024
 * @Author suroj.awal
 */

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
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final WebClient.Builder webClient;
    private static final SimpleTemplateEngine engine = new SimpleTemplateEngine();

    @Value("${sms.username}")
    private String username;

    @Value("${sms.password}")
    private String password;

    @Value("${sms.organizationCode}")
    private String organizationCode;

    public SmsResponse sendSMS(CommunicationRequest communicationRequest, SmsRecord smsRecord) throws GlobalException {
        // Prepare request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username, password);
        headers.set("OrganisationCode", organizationCode);

        try {
            // Prepare request body
            SmsRequest smsRequest = SmsRequest.builder()
                    .isClientLogin("N")
                    .receiverNo(communicationRequest.getMobile().toString())
                    .message(engine.createTemplate(smsRecord.body()).make(communicationRequest.getData()).toString())
                    .username(username)
                    .password(password)
                    .organisationCode(organizationCode)
                    .build();
            // Make the request
            return webClient.build().post()
                    .uri("https://fastapi.jojolapatech.hrmsuite.com.np:8080/api/Sms/ExecuteSendSms")
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .bodyValue(smsRequest)
                    .retrieve()
                    .bodyToMono(SmsResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", (Object) e.getStackTrace());
            throw new GlobalException("SMS001", e);
        }
    }


    public SmsResponse sendBulkSMS(List<String> mobile, SmsRecord smsRecord) throws IOException, ClassNotFoundException {
        // Prepare request headers
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
        // Prepare request body
        SmsRequest smsRequest = SmsRequest.builder()
                .isClientLogin("N")
                .date(new Date())
                .batchId("b0")
                .smsDetails(bulkSMSRequests)
                .username(username)
                .password(password)
                .organisationCode(organizationCode)
                .build();

        // Make the request for single sms
        return webClient.build().post()
                .uri("https://fastapi.jojolapatech.hrmsuite.com.np:8080/api/Sms/ExecuteSendSms")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue(smsRequest)
                .retrieve()
                .bodyToMono(SmsResponse.class)
                .block();


    }


}
