package com.nepbrain.technology.communication.service.impl;

import com.nepbrain.technology.communication.client.UserProviderGrpcClient;
import com.nepbrain.technology.communication.entity.UserNotificationEntity;
import com.nepbrain.technology.communication.enums.ChannelEnum;
import com.nepbrain.technology.communication.enums.ErrorCodeEnum;
import com.nepbrain.technology.communication.enums.LanguageEnum;
import com.nepbrain.technology.communication.enums.PlatformEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import com.nepbrain.technology.communication.payload.request.CommunicationRequest;
import com.nepbrain.technology.communication.payload.request.CreateUserNotificationRequest;
import com.nepbrain.technology.communication.payload.request.PaginationRequest;
import com.nepbrain.technology.communication.payload.request.UserNotificationListRequest;
import com.nepbrain.technology.communication.payload.response.UserNotificationListResponse;
import com.nepbrain.technology.communication.payload.response.UserNotificationResponse;
import com.nepbrain.technology.communication.payload.response.UserNotificationUnreadCountResponse;
import com.nepbrain.technology.communication.repository.UserNotificationRepository;
import com.nepbrain.technology.communication.service.CommunicationService;
import com.nepbrain.technology.communication.service.UserNotificationService;
import com.nepbrain.technology.communication.util.Helper;
import com.nepbrain.technology.core.config.security.CurrentUser;
import com.nepbrain.technology.core.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationRepository notificationRepository;
    private final UserProviderGrpcClient userProviderGrpcClient;
    private final CommunicationService communicationService;

    @Override
    @Transactional(readOnly = true)
    public UserNotificationListResponse listForCurrentUser(UserNotificationListRequest request) {
        return listForUser(requireCurrentUserId(), request);
    }

    @Override
    @Transactional(readOnly = true)
    public UserNotificationListResponse listForUser(UUID userId, UserNotificationListRequest request) {
        return listForUserInternal(userId, request, false);
    }

    @Override
    @Transactional(readOnly = true)
    public UserNotificationListResponse listWorkflowForUser(UUID userId, UserNotificationListRequest request) {
        return listForUserInternal(userId, request, true);
    }

    private UserNotificationListResponse listForUserInternal(
            UUID userId, UserNotificationListRequest request, boolean excludeCompanyNotice) {
        Page<UserNotificationEntity> page = notificationRepository.findForUser(
                userId,
                request != null ? request.getRead() : null,
                excludeCompanyNotice,
                toSearchPattern(request != null ? request.getSearch() : null),
                Helper.getPagination(toPaginationRequest(request)));
        List<UserNotificationResponse> rows = page.getContent().stream()
                .map(this::toResponse)
                .toList();
        long unread = excludeCompanyNotice
                ? notificationRepository.countWorkflowUnread(userId)
                : notificationRepository.countByUserIdAndReadFalseAndArchivedFalse(userId);
        return UserNotificationListResponse.builder()
                .result(rows)
                .totalElements(page.getTotalElements())
                .unreadCount(unread)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserNotificationUnreadCountResponse unreadCountForCurrentUser() {
        return unreadCountForUser(requireCurrentUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserNotificationUnreadCountResponse unreadCountForUser(UUID userId) {
        long unread = notificationRepository.countByUserIdAndReadFalseAndArchivedFalse(userId);
        return UserNotificationUnreadCountResponse.builder()
                .unreadCount(unread)
                .count(unread)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserNotificationUnreadCountResponse workflowUnreadCountForUser(UUID userId) {
        long unread = notificationRepository.countWorkflowUnread(userId);
        return UserNotificationUnreadCountResponse.builder()
                .unreadCount(unread)
                .count(unread)
                .build();
    }

    @Override
    public void markRead(UUID notificationId) {
        markReadForUser(requireCurrentUserId(), notificationId);
    }

    @Override
    public void markAllRead() {
        markAllReadForUser(requireCurrentUserId());
    }

    @Override
    public void markReadForUser(UUID userId, UUID notificationId) {
        UserNotificationEntity entity = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new GlobalException(
                        ErrorCodeEnum.NOT001.name(),
                        ErrorCodeEnum.NOT001.getValue(),
                        HttpStatus.NOT_FOUND));
        if (!entity.isRead()) {
            entity.setRead(true);
            entity.setReadAt(LocalDateTime.now());
            notificationRepository.save(entity);
        }
    }

    @Override
    public void markAllReadForUser(UUID userId) {
        List<UserNotificationEntity> unread = notificationRepository.findByUserIdAndReadFalseAndArchivedFalse(userId);
        if (unread.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (UserNotificationEntity entity : unread) {
            entity.setRead(true);
            entity.setReadAt(now);
        }
        notificationRepository.saveAll(unread);
    }

    @Override
    public void create(CreateUserNotificationRequest request) {
        UserProviderGrpcClient.UserContactDto recipient = null;
        boolean needsContact = Boolean.TRUE.equals(request.getSendEmail());
        if (needsContact) {
            List<UserProviderGrpcClient.UserContactDto> contacts = userProviderGrpcClient.getUsersForBulkMessage(
                    false,
                    null,
                    List.of(request.getRecipientUserId().toString()));
            if (contacts.isEmpty()) {
                throw new GlobalException(
                        ErrorCodeEnum.NOT003.name(),
                        ErrorCodeEnum.NOT003.getValue(),
                        HttpStatus.NOT_FOUND);
            }
            recipient = contacts.getFirst();
        } else {
            // In-app only: do not block on User-Service lookup (common cause of empty bell).
            try {
                List<UserProviderGrpcClient.UserContactDto> contacts = userProviderGrpcClient.getUsersForBulkMessage(
                        false,
                        null,
                        List.of(request.getRecipientUserId().toString()));
                if (!contacts.isEmpty()) {
                    recipient = contacts.getFirst();
                }
            } catch (Exception ex) {
                log.warn("Optional recipient lookup skipped for in-app notification {}: {}",
                        request.getRecipientUserId(), ex.getMessage());
            }
        }

        UserNotificationEntity entity = UserNotificationEntity.builder()
                .userId(request.getRecipientUserId())
                .recipientEmployeeId(request.getRecipientEmployeeId())
                .companyId(request.getCompanyId())
                .branchId(request.getBranchId())
                .title(request.getTitle().trim())
                .body(com.nepbrain.technology.communication.util.HtmlPlainText.of(request.getBody().trim()))
                .topic(trimToNull(request.getTopic()))
                .notificationType(trimToNull(request.getNotificationType()))
                .category(trimToNull(request.getCategory()))
                .referenceType(trimToNull(request.getReferenceType()))
                .referenceId(trimToNull(request.getReferenceId()))
                .requestType(trimToNull(request.getRequestType()))
                .requestId(request.getRequestId())
                .requestNumber(trimToNull(request.getRequestNumber()))
                .workflowHistoryId(request.getWorkflowHistoryId())
                .actionUrl(trimToNull(request.getActionUrl()))
                .actionLabel(trimToNull(request.getActionLabel()))
                .priority(trimToNull(request.getPriority()))
                .channel(trimToNull(request.getChannel()) != null ? request.getChannel().trim() : "IN_APP")
                .metadata(trimToNull(request.getMetadata()))
                .read(false)
                .archived(false)
                .build();
        try {
            notificationRepository.save(entity);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            log.info("Duplicate in-app notification skipped for user {} type {} history {}",
                    request.getRecipientUserId(), request.getNotificationType(), request.getWorkflowHistoryId());
            return;
        }

        if (Boolean.TRUE.equals(request.getSendEmail()) && recipient != null && StringUtils.hasText(recipient.getEmail())) {
            sendOptionalEmail(recipient, request);
        }
    }

    private void sendOptionalEmail(
            UserProviderGrpcClient.UserContactDto recipient,
            CreateUserNotificationRequest request) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("TITLE", request.getTitle());
            data.put("BODY", request.getBody());
            data.put("NAME", StringUtils.hasText(recipient.getFullName()) ? recipient.getFullName() : recipient.getEmail());

            CommunicationRequest communicationRequest = CommunicationRequest.builder()
                    .username(recipient.getEmail())
                    .email(recipient.getEmail())
                    .platform(PlatformEnum.WEB)
                    .topic(parseTopic(request.getTopic()))
                    .language(LanguageEnum.EN)
                    .sendTo(ChannelEnum.EMAIL)
                    .data(data)
                    .companyId(request.getCompanyId())
                    .build();

            if (StringUtils.hasText(request.getEmailSubject()) && StringUtils.hasText(request.getEmailBody())) {
                communicationService.sendWithExplicitContent(
                        communicationRequest,
                        true,
                        false,
                        false,
                        request.getEmailSubject(),
                        request.getEmailBody(),
                        null,
                        null,
                        null);
            } else {
                communicationService.send(communicationRequest);
            }
        } catch (Exception ex) {
            log.warn("Optional notification email failed for user {}: {}", recipient.getId(), ex.getMessage());
        }
    }

    private UserNotificationResponse toResponse(UserNotificationEntity entity) {
        return UserNotificationResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .body(com.nepbrain.technology.communication.util.HtmlPlainText.of(entity.getBody()))
                .read(entity.isRead())
                .topic(entity.getTopic())
                .notificationType(entity.getNotificationType())
                .category(entity.getCategory())
                .referenceType(entity.getReferenceType())
                .referenceId(entity.getReferenceId())
                .requestType(entity.getRequestType())
                .requestId(entity.getRequestId())
                .actionUrl(entity.getActionUrl())
                .actionLabel(entity.getActionLabel())
                .metadata(entity.getMetadata())
                .recipientEmployeeId(entity.getRecipientEmployeeId())
                .createdAt(entity.getCreatedAt())
                .readAt(entity.getReadAt())
                .build();
    }

    private UUID requireCurrentUserId() {
        UUID userId = CurrentUser.getUserAsUuidBlocking();
        if (userId == null) {
            throw new GlobalException(
                    ErrorCodeEnum.NOT002.name(),
                    ErrorCodeEnum.NOT002.getValue(),
                    HttpStatus.UNAUTHORIZED);
        }
        return userId;
    }

    private static PaginationRequest toPaginationRequest(UserNotificationListRequest request) {
        PaginationRequest pagination = new PaginationRequest();
        if (request == null) {
            pagination.setPageNo(0);
            pagination.setPageSize(10);
            pagination.setSortBy("createdAt");
            pagination.setDirection("desc");
            return pagination;
        }
        pagination.setPageNo(Math.max(request.getPageNo(), 0));
        pagination.setPageSize(request.getPageSize() > 0 ? request.getPageSize() : 10);
        pagination.setSortBy(StringUtils.hasText(request.getSortBy()) ? request.getSortBy() : "createdAt");
        pagination.setDirection(StringUtils.hasText(request.getDirection()) ? request.getDirection() : "desc");
        return pagination;
    }

    private static String toSearchPattern(String search) {
        if (!StringUtils.hasText(search)) {
            return null;
        }
        return "%" + search.trim().toLowerCase() + "%";
    }

    private static TopicEnum parseTopic(String topic) {
        if (!StringUtils.hasText(topic)) {
            return TopicEnum.BULK;
        }
        try {
            return TopicEnum.valueOf(topic.trim().toUpperCase());
        } catch (Exception ex) {
            return TopicEnum.BULK;
        }
    }

    private static String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
