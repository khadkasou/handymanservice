package com.nepbrain.technology.communication.service.impl;

import com.nepbrain.technology.communication.entity.MessageDetailEntity;
import com.nepbrain.technology.communication.entity.MessageEntity;
import com.nepbrain.technology.communication.enums.ErrorCodeEnum;
import com.nepbrain.technology.communication.enums.StatusEnum;
import com.nepbrain.technology.communication.payload.request.MessageDataRequest;
import com.nepbrain.technology.communication.payload.request.MessageDetailRequest;
import com.nepbrain.technology.communication.payload.request.MessageRequest;
import com.nepbrain.technology.communication.payload.response.DatapaginationResponse;
import com.nepbrain.technology.communication.payload.response.MessageDetailResponse;
import com.nepbrain.technology.communication.payload.response.MessageResponse;
import com.nepbrain.technology.communication.repository.MessageDetailRepository;
import com.nepbrain.technology.communication.repository.MessageRepository;
import com.nepbrain.technology.communication.service.MessageService;
import com.nepbrain.technology.communication.service.specification.MessageSpecification;
import com.nepbrain.technology.core.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/*
 * @Created At 03/03/2024
 * @Author suroj.awal
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageDetailRepository messageDetailRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    @Transactional
    public void create(MessageRequest request) {
        if (request.getTopic() != null && messageRepository.existsByTopic(request.getTopic())) {
            throw new GlobalException(ErrorCodeEnum.MSG001.name());
        }
        MessageEntity message = MessageEntity.builder()
                .topic(request.getTopic())
                .status(request.getStatus())
                .enableSms(request.getEnableSms() != null ? request.getEnableSms() : false)
                .enableEmail(request.getEnableEmail() != null ? request.getEnableEmail() : false)
                .enableNotification(request.getEnableNotification() != null ? request.getEnableNotification() : false)
                .build();
        message = messageRepository.save(message);
        if (request.getDetails() != null && !request.getDetails().isEmpty()) {
            List<MessageDetailEntity> details = new ArrayList<>();
            for (MessageDetailRequest dr : request.getDetails()) {
                MessageDetailEntity detail = MessageDetailEntity.builder()
                        .message(message)
                        .language(dr.getLanguage())
                        .emailSubject(dr.getEmailSubject())
                        .emailBody(dr.getEmailBody())
                        .smsBody(dr.getSmsBody())
                        .notificationSubject(dr.getNotificationSubject())
                        .notificationBody(dr.getNotificationBody())
                        .build();
                details.add(messageDetailRepository.save(detail));
            }
            message.setMessages(details);
        }
    }

    @Override
    @Transactional
    public void update(MessageRequest request) {
        if (request.getId() == null) {
            throw new GlobalException(ErrorCodeEnum.MSG002.name());
        }
        MessageEntity message = messageRepository.findById(request.getId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.MSG002.name()));
        message.setTopic(request.getTopic());
        message.setStatus(request.getStatus());
        message.setEnableSms(request.getEnableSms() != null ? request.getEnableSms() : false);
        message.setEnableEmail(request.getEnableEmail() != null ? request.getEnableEmail() : false);
        message.setEnableNotification(request.getEnableNotification() != null ? request.getEnableNotification() : false);
        messageRepository.save(message);
    }

    @Override
    public MessageResponse getById(UUID id) {
        MessageEntity message = messageRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.MSG002.name()));
        return toResponse(message);
    }

    @Override
    public DatapaginationResponse getAll(MessageDataRequest request) {
        Specification<MessageEntity> spec = MessageSpecification.messageFilter(request);
        Sort sort = Sort.by(Sort.Direction.fromString(
                request.getDirection() != null && request.getDirection().equalsIgnoreCase("desc") ? "desc" : "asc"),
                request.getSortBy() != null ? request.getSortBy() : "createdDate");
        int pageNo = Math.max(0, request.getPageNo());
        int pageSize = request.getPageSize() <= 0 ? 10 : request.getPageSize();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<MessageEntity> page = messageRepository.findAll(spec, pageable);
        List<MessageResponse> result = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return DatapaginationResponse.builder()
                .totalElementCount(page.getTotalElements())
                .result(result)
                .build();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        MessageEntity message = messageRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.MSG002.name()));
        List<MessageDetailEntity> details = messageDetailRepository.findAllByMessage(message);
        messageDetailRepository.deleteAll(details);
        messageRepository.delete(message);
    }

    @Override
    public void changeStatus(UUID id, StatusEnum status) {
        MessageEntity message = messageRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.MSG002.name()));
        message.setStatus(status);
        messageRepository.save(message);
    }

    @Override
    @Transactional
    public MessageDetailResponse addDetail(UUID messageId, MessageDetailRequest request) {
        if (request.getLanguage() == null) {
            throw new GlobalException(ErrorCodeEnum.MSG003.name());
        }
        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.MSG002.name()));
        MessageDetailEntity detail = messageDetailRepository
                .findByMessageAndLanguage(message, request.getLanguage())
                .orElse(null);
        if (detail == null) {
            detail = MessageDetailEntity.builder()
                    .message(message)
                    .language(request.getLanguage())
                    .build();
        }
        if (request.getEmailSubject() != null) {
            detail.setEmailSubject(request.getEmailSubject());
        }
        if (request.getEmailBody() != null) {
            detail.setEmailBody(request.getEmailBody());
        }
        if (request.getSmsBody() != null) {
            detail.setSmsBody(request.getSmsBody());
        }
        if (request.getNotificationSubject() != null) {
            detail.setNotificationSubject(request.getNotificationSubject());
        }
        if (request.getNotificationBody() != null) {
            detail.setNotificationBody(request.getNotificationBody());
        }
        detail = messageDetailRepository.save(detail);
        return modelMapper.map(detail, MessageDetailResponse.class);
    }

    @Override
    public MessageDetailResponse updateDetail(UUID detailId, MessageDetailRequest request) {
        MessageDetailEntity detail = messageDetailRepository.findById(detailId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.MSG003.name()));
        if (request.getLanguage() != null) {
            detail.setLanguage(request.getLanguage());
        }
        if (request.getEmailSubject() != null) {
            detail.setEmailSubject(request.getEmailSubject());
        }
        if (request.getEmailBody() != null) {
            detail.setEmailBody(request.getEmailBody());
        }
        if (request.getSmsBody() != null) {
            detail.setSmsBody(request.getSmsBody());
        }
        if (request.getNotificationSubject() != null) {
            detail.setNotificationSubject(request.getNotificationSubject());
        }
        if (request.getNotificationBody() != null) {
            detail.setNotificationBody(request.getNotificationBody());
        }
        detail = messageDetailRepository.save(detail);
        return modelMapper.map(detail, MessageDetailResponse.class);
    }

    private MessageResponse toResponse(MessageEntity message) {
        MessageResponse response = modelMapper.map(message, MessageResponse.class);
        if (message.getMessages() != null) {
            List<MessageDetailResponse> detailResponses = message.getMessages().stream()
                    .map(d -> modelMapper.map(d, MessageDetailResponse.class))
                    .collect(Collectors.toList());
            response.setDetails(detailResponses);
        }
        return response;
    }
}
