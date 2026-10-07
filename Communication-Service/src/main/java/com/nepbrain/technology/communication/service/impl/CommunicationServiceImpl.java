package com.nepbrain.technology.communication.service.impl;

import com.nepbrain.technology.communication.entity.CommunicationEntity;
import com.nepbrain.technology.communication.entity.MessageDetailEntity;
import com.nepbrain.technology.communication.entity.MessageEntity;
import com.nepbrain.technology.communication.entity.OTPEntity;
import com.nepbrain.technology.communication.enums.*;
import com.nepbrain.technology.communication.payload.request.CommunicationRequest;
import com.nepbrain.technology.communication.payload.request.CommunicationSearchFilterPaginationRequest;
import com.nepbrain.technology.communication.payload.request.NotificationMessageRequest;
import com.nepbrain.technology.communication.payload.request.OTPVerifyRequest;
import com.nepbrain.technology.communication.payload.response.CommunicationLogResponse;
import com.nepbrain.technology.communication.payload.response.DatapaginationResponse;
import com.nepbrain.technology.communication.payload.response.TopicEnumResponse;
import com.nepbrain.technology.communication.record.EmailRecord;
import com.nepbrain.technology.communication.record.SmsRecord;
import com.nepbrain.technology.communication.repository.CommunicationRepository;
import com.nepbrain.technology.communication.repository.MessageRepository;
import com.nepbrain.technology.communication.repository.OTPRepository;
import com.nepbrain.technology.communication.service.CommunicationService;
import com.nepbrain.technology.communication.service.EmailService;
import com.nepbrain.technology.communication.service.FirebaseNotificationService;
import com.nepbrain.technology.communication.service.SmsService;
import com.nepbrain.technology.communication.service.specification.CommunicationSpecification;
import com.nepbrain.technology.communication.util.GeneralUtil;
import com.nepbrain.technology.communication.util.Helper;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import com.nepbrain.technology.core.service.AesService;
import groovy.text.SimpleTemplateEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommunicationServiceImpl implements CommunicationService {

    @Value("${otp.length}")
    private Integer otpLength;
    @Value("${otp.expiry-time-second}")
    private Long otpDuration;

    private final MessageRepository messageRepository;
    private final EmailService emailService;
    private final GeneralUtil generalUtil;
    private final OTPRepository otpRepository;

    private final CommunicationRepository communicationRepository;

    private final SmsService smsService;
    private final FirebaseNotificationService firebaseNotificationService;
    private final AesService aesService;
    private static final SimpleTemplateEngine engine = new SimpleTemplateEngine();

    @Override
    public String send(CommunicationRequest request) throws GlobalException {
        MessageEntity message = messageRepository.findByTopic(request.getTopic());
        MessageDetailEntity messageDetail = message.getMessages()
                .stream()
                .parallel()
                .filter(m -> m.getLanguage()
                        .equals(LanguageEnum.EN))
                .findAny()
                .orElse(new MessageDetailEntity());
        if (Boolean.TRUE.equals(message.getEnableEmail())) {
            //Save send email history
            CommunicationEntity communication = CommunicationEntity.builder()
                    .topic(request.getTopic())
                    .username(request.getUsername())
                    .language(request.getLanguage())
                    .subject(messageDetail.getEmailSubject())
                    .type(ChannelEnum.EMAIL)
                    .build();
            try {
                communication.setMessage(engine.createTemplate(messageDetail.getEmailBody())
                        .make(request.getData())
                        .toString());
                emailService.sendEmail(request, new EmailRecord(messageDetail.getEmailSubject(), messageDetail.getEmailBody()));
                communication.setStatus(StatusEnum.DELIVER);
            } catch (Exception ex) {
                communication.setStatus(StatusEnum.FAILED);
                communication.setResponse(ex.getMessage());
            }
            communicationRepository.save(communication);
        }

        if (Boolean.TRUE.equals(message.getEnableSms()) && request.getMobile() != null) {
            String smsBody = messageDetail.getSmsBody() != null ? messageDetail.getSmsBody() : "";
            //Save send message history
            CommunicationEntity communication = CommunicationEntity.builder()
                    .topic(request.getTopic())
                    .username(request.getMobile().toString())
                    .language(request.getLanguage())
                    .type(ChannelEnum.SMS)
                    .build();
            try {
                communication.setMessage(engine.createTemplate(smsBody)
                        .make(request.getData())
                        .toString());
                smsService.sendSMS(request, new SmsRecord(smsBody));
                communication.setStatus(StatusEnum.DELIVER);
            } catch (Exception e) {
                communication.setStatus(StatusEnum.FAILED);
                communication.setResponse(e.getMessage());
            }
            communicationRepository.save(communication);
        }

        if (Boolean.TRUE.equals(message.getEnableNotification())) {
            String notificationBody = messageDetail.getNotificationBody() != null ? messageDetail.getNotificationBody() : "";
            String channelUsername = request.getMobile() != null ? request.getMobile().toString()
                    : (request.getUsername() != null ? request.getUsername() : (request.getEmail() != null ? request.getEmail() : ""));
            CommunicationEntity communication = CommunicationEntity.builder()
                    .topic(request.getTopic())
                    .username(channelUsername)
                    .language(request.getLanguage())
                    .type(ChannelEnum.NOTIFICATION)
                    .build();
            try {
                //Save send message history
                String resolvedBody = engine.createTemplate(notificationBody)
                        .make(request.getData() != null ? request.getData() : new HashMap<>())
                        .toString();
                communication.setMessage(resolvedBody);

                if (request.getTokens() != null && !request.getTokens().isEmpty()) {
                    firebaseNotificationService.sendNotification(NotificationMessageRequest.builder()
                            .title(messageDetail.getNotificationSubject())
                            .body(resolvedBody)
                            .data(request.getData())
                            .fcmId(request.getTokens().get(0))
                            .build());
                }
                communication.setStatus(StatusEnum.DELIVER);
            } catch (Exception e) {
                communication.setStatus(StatusEnum.FAILED);
                communication.setResponse(e.getMessage());
            }
            communicationRepository.save(communication);
        }
        return "success";
    }

    @Override
    public void sendWithExplicitContent(CommunicationRequest request,
                                         boolean sendEmail, boolean sendSms, boolean sendNotification,
                                         String emailSubject, String emailBody, String smsBody,
                                         String notificationTitle, String notificationBody) throws GlobalException {
        if (request.getData() == null) {
            request.setData(new HashMap<>());
        }
        if (sendEmail && emailSubject != null && emailBody != null && request.getEmail() != null && !request.getEmail().isBlank()) {
            CommunicationEntity communication = CommunicationEntity.builder()
                    .topic(TopicEnum.BULK)
                    .username(request.getUsername() != null ? request.getUsername() : request.getEmail())
                    .language(request.getLanguage() != null ? request.getLanguage() : LanguageEnum.EN)
                    .subject(emailSubject)
                    .type(ChannelEnum.EMAIL)
                    .build();
            try {
                String subj = engine.createTemplate(emailSubject).make(request.getData()).toString();
                String body = engine.createTemplate(emailBody).make(request.getData()).toString();
                communication.setMessage(body);
                emailService.sendEmail(request, new EmailRecord(subj, body));
                communication.setStatus(StatusEnum.DELIVER);
            } catch (Exception ex) {
                communication.setStatus(StatusEnum.FAILED);
                communication.setResponse(ex.getMessage());
            }
            communicationRepository.save(communication);
        }
        if (sendSms && smsBody != null && request.getMobile() != null) {
            CommunicationEntity communication = CommunicationEntity.builder()
                    .topic(TopicEnum.BULK)
                    .username(request.getMobile().toString())
                    .language(request.getLanguage() != null ? request.getLanguage() : LanguageEnum.EN)
                    .type(ChannelEnum.SMS)
                    .build();
            try {
                String body = engine.createTemplate(smsBody).make(request.getData()).toString();
                communication.setMessage(body);
                smsService.sendSMS(request, new SmsRecord(body));
                communication.setStatus(StatusEnum.DELIVER);
            } catch (Exception e) {
                communication.setStatus(StatusEnum.FAILED);
                communication.setResponse(e.getMessage());
            }
            communicationRepository.save(communication);
        }
        if (sendNotification && notificationTitle != null && notificationBody != null
                && request.getTokens() != null && !request.getTokens().isEmpty()) {
            CommunicationEntity communication = CommunicationEntity.builder()
                    .topic(TopicEnum.BULK)
                    .username(request.getUsername() != null ? request.getUsername() : "")
                    .language(request.getLanguage() != null ? request.getLanguage() : LanguageEnum.EN)
                    .type(ChannelEnum.NOTIFICATION)
                    .build();
            try {
                String title = engine.createTemplate(notificationTitle).make(request.getData()).toString();
                String body = engine.createTemplate(notificationBody).make(request.getData()).toString();
                communication.setMessage(body);
                firebaseNotificationService.sendNotification(NotificationMessageRequest.builder()
                        .title(title)
                        .body(body)
                        .data(request.getData())
                        .fcmId(request.getTokens().get(0))
                        .build());
                communication.setStatus(StatusEnum.DELIVER);
            } catch (Exception e) {
                communication.setStatus(StatusEnum.FAILED);
                communication.setResponse(e.getMessage());
            }
            communicationRepository.save(communication);
        }
    }

    @Override
    public GlobalResponse generateOTP(CommunicationRequest request) throws GlobalException {
        if (request.getLanguage() == null) {
            request.setLanguage(LanguageEnum.EN);
        }
        Long code = generalUtil.generateOTP(4);
        log.info("OTP generated: " + code);

        Map<String, String> data = new HashMap<>();
        data.put("CODE", code.toString());
        request.setData(data);
        send(request);

        Map<String, Object> otpData = new HashMap<>();
        LocalDateTime expireAt = LocalDateTime.now()
                .plusSeconds(otpDuration);
        otpData.put("expireAt", expireAt);
        otpData.put("sendTo", request.getEmail());
        log.info("DATE {}", expireAt);
        //Update Previous OTP code
        OTPEntity otp = otpRepository.findByUsernameAndTopicAndStatus(request.getUsername(), request.getTopic(),
                OTPStatusEnum.PENDING);
        if (otp != null) {
            otp.setStatus(OTPStatusEnum.FAILED);
            otpRepository.save(otp);
        }
        OTPEntity otpEntity = OTPEntity.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .code(aesService.encrypt(code.toString()))
                .topic(request.getTopic())
                .status(OTPStatusEnum.PENDING)
                .build();
        otpRepository.save(otpEntity);
        return GlobalResponseBuilder.buildSuccessResponse( "Otp Sent success", otpData);
    }

    @Override
    public GlobalResponse verifyOTP(OTPVerifyRequest request) throws GlobalException {
        OTPEntity otpEntity = otpRepository.findFirstByUsernameAndTopicOrderByCreatedAtDesc(request.getUsername(), request.getTopic());
        if (otpEntity == null) {
            throw new GlobalException("OTP001");
        }
        if (request.getCode()
                .length() != otpLength) {
            throw new GlobalException("OTP002");
        }
        if (otpEntity.getStatus().equals(OTPStatusEnum.FAILED)) {
            return GlobalResponseBuilder.buildFailResponse("Otp expired.");
        }
        long secondBetween = ChronoUnit.SECONDS.between(otpEntity.getCreatedAt(), LocalDateTime.now());

        if (secondBetween >= otpDuration) {
            otpEntity.setStatus(OTPStatusEnum.FAILED);
            otpRepository.save(otpEntity);
            return GlobalResponseBuilder.buildFailResponse("Otp expired.");
        }
        if (!request.getCode()
                .equals(aesService.decrypt(otpEntity.getCode()))) {
            throw new GlobalException("OTP004");
        }
        otpEntity.setStatus(OTPStatusEnum.VERIFIED);
        otpRepository.save(otpEntity);
        return GlobalResponseBuilder.buildSuccessResponse(null, "OTP verified successfully.");
    }

    @Override
    public GlobalResponse getCommunicationsLog(CommunicationSearchFilterPaginationRequest request) {
        Pageable pageable = Helper.getPagination(request);
        Specification<CommunicationEntity> specification = CommunicationSpecification.getCommunications(request);
        Page<CommunicationEntity> communicationEntityPage = communicationRepository.findAll(specification, pageable);
        List<CommunicationEntity> communications = communicationEntityPage.getContent();
        List<CommunicationLogResponse> communicationLogResponses = communications.stream()
                .map(communicationEntity -> new ModelMapper().map(communicationEntity, CommunicationLogResponse.class))
                .toList();
        DatapaginationResponse datapaginationResponse = DatapaginationResponse.builder()
                .totalElementCount(communicationEntityPage.getTotalElements())
                .result(communicationLogResponses)
                .build();
        return GlobalResponseBuilder.buildSuccessResponse("", datapaginationResponse);
    }

    @Override
    public GlobalResponse getTopicEnums() {
        return getTopicEnums(null);
    }

    @Override
    public GlobalResponse getTopicEnums(MessageModuleEnum module) {
        List<TopicEnumResponse> topicEnumResponses = new ArrayList<>();
        for (TopicEnum topicEnum : TopicEnum.values()) {
            if (module != null && topicEnum.getModule() != module) {
                continue;
            }
            TopicEnumResponse topicEnumResponse = TopicEnumResponse.builder()
                    .key(topicEnum.name())
                    .value(topicEnum.getValue())
                    .module(topicEnum.getModule().name())
                    .moduleDisplayName(topicEnum.getModule().getDisplayName())
                    .build();
            topicEnumResponses.add(topicEnumResponse);
        }
        return GlobalResponseBuilder.buildSuccessResponse("", topicEnumResponses);
    }

}

