package com.nepbrain.technology.communication.grpc;

import com.nepbrain.technology.communication.enums.ChannelEnum;
import com.nepbrain.technology.communication.enums.LanguageEnum;
import com.nepbrain.technology.communication.enums.PlatformEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import com.nepbrain.technology.communication.payload.request.BulkSendRequest;
import com.nepbrain.technology.communication.payload.request.CommunicationRequest;
import com.nepbrain.technology.communication.payload.request.CreateUserNotificationRequest;
import com.nepbrain.technology.communication.payload.request.UserNotificationListRequest;
import com.nepbrain.technology.communication.payload.response.UserNotificationListResponse;
import com.nepbrain.technology.communication.payload.response.UserNotificationResponse;
import com.nepbrain.technology.communication.payload.response.UserNotificationUnreadCountResponse;
import com.nepbrain.technology.communication.service.BulkSendService;
import com.nepbrain.technology.communication.service.CommunicationService;
import com.nepbrain.technology.communication.service.UserNotificationService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CommunicationTemplateGrpcService extends CommunicationTemplateServiceGrpc.CommunicationTemplateServiceImplBase {

    private final CommunicationService communicationService;
    private final BulkSendService bulkSendService;
    private final UserNotificationService userNotificationService;

    @Override
    public void sendTemplateEmail(
            SendTemplateEmailRequest request,
            StreamObserver<SendTemplateEmailResponse> responseObserver
    ) {
        try {
            TopicEnum topic = parseTopic(request.getTopic());
            LanguageEnum language = parseLanguage(request.getLanguage());

            Map<String, String> payloadData = new HashMap<>(request.getDataMap());
            if (request.getName() != null && !request.getName().isBlank()) {
                payloadData.putIfAbsent("NAME", request.getName());
            }
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                payloadData.putIfAbsent("EMAIL", request.getEmail());
            }
            if (request.getUsername() != null && !request.getUsername().isBlank()) {
                payloadData.putIfAbsent("USERNAME", request.getUsername());
            }

            CommunicationRequest communicationRequest = CommunicationRequest.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .platform(PlatformEnum.WEB)
                    .topic(topic)
                    .language(language)
                    .sendTo(ChannelEnum.EMAIL)
                    .data(payloadData)
                    .companyId(resolveCompanyId(request.getCompanyId(), payloadData))
                    .mobile(parseMobile(request.getMobile()))
                    .build();

            communicationService.send(communicationRequest);
            responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Email sent successfully")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Failed to send template email via gRPC (topic={}): {}",
                    request.getTopic(), ex.getMessage(), ex);
            responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(ex.getMessage() == null ? "Failed to send email" : ex.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void sendTemplateNotification(
            SendTemplateNotificationRequest request,
            StreamObserver<SendTemplateEmailResponse> responseObserver
    ) {
        try {
            if (request.getTokensList() == null || request.getTokensList().isEmpty()) {
                responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("No FCM tokens provided")
                        .build());
                responseObserver.onCompleted();
                return;
            }

            TopicEnum topic = parseTopic(request.getTopic());
            LanguageEnum language = parseLanguage(request.getLanguage());

            Map<String, String> payloadData = new HashMap<>(request.getDataMap());
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                payloadData.putIfAbsent("EMAIL", request.getEmail());
            }
            if (request.getUsername() != null && !request.getUsername().isBlank()) {
                payloadData.putIfAbsent("USERNAME", request.getUsername());
            }

            CommunicationRequest communicationRequest = CommunicationRequest.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .platform(PlatformEnum.WEB)
                    .topic(topic)
                    .language(language)
                    .sendTo(ChannelEnum.NOTIFICATION)
                    .tokens(new ArrayList<>(request.getTokensList()))
                    .data(payloadData)
                    .companyId(resolveCompanyId(request.getCompanyId(), payloadData))
                    .build();

            communicationService.send(communicationRequest);
            responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Notification sent successfully")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Failed to send template notification via gRPC", ex);
            responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(ex.getMessage() == null ? "Failed to send notification" : ex.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void sendBulk(BulkSendGrpcRequest request, StreamObserver<SendTemplateEmailResponse> responseObserver) {
        try {
            BulkSendRequest bulkRequest = BulkSendRequest.builder()
                    .targetType(mapTargetType(request.getTargetType()))
                    .roleIds(request.getRoleIdsList())
                    .userIds(request.getUserIdsList())
                    .sendEmail(request.getSendEmail())
                    .sendSms(request.getSendSms())
                    .sendNotification(request.getSendNotification())
                    .emailSubject(request.getEmailSubject())
                    .emailBody(request.getEmailBody())
                    .smsBody(request.getSmsBody())
                    .notificationTitle(request.getNotificationTitle())
                    .notificationBody(request.getNotificationBody())
                    .companyId(parseUuid(request.getCompanyId()))
                    .build();
            bulkSendService.sendBulk(bulkRequest);
            responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Bulk send completed")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Failed to send bulk via gRPC", ex);
            responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(ex.getMessage() == null ? "Bulk send failed" : ex.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void sendExplicitEmail(
            SendExplicitEmailRequest request,
            StreamObserver<SendTemplateEmailResponse> responseObserver) {
        try {
            if (!StringUtils.hasText(request.getEmail())
                    || !StringUtils.hasText(request.getSubject())
                    || !StringUtils.hasText(request.getBody())) {
                responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("email, subject, and body are required")
                        .build());
                responseObserver.onCompleted();
                return;
            }
            CommunicationRequest communicationRequest = CommunicationRequest.builder()
                    .username(StringUtils.hasText(request.getUsername())
                            ? request.getUsername()
                            : request.getEmail())
                    .email(request.getEmail().trim())
                    .platform(PlatformEnum.WEB)
                    .topic(TopicEnum.BULK)
                    .language(LanguageEnum.EN)
                    .sendTo(ChannelEnum.EMAIL)
                    .companyId(parseUuid(request.getCompanyId()))
                    .build();
            // null data => EmailService skips Groovy and sends HTML as-is
            communicationService.sendWithExplicitContent(
                    communicationRequest,
                    true,
                    false,
                    false,
                    request.getSubject(),
                    request.getBody(),
                    null,
                    null,
                    null);
            responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Email sent successfully")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Failed to send explicit email via gRPC to {}: {}", request.getEmail(), ex.getMessage(), ex);
            responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(ex.getMessage() == null ? "Failed to send email" : ex.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void createUserNotification(
            CreateUserNotificationGrpcRequest request,
            StreamObserver<SendTemplateEmailResponse> responseObserver) {
        try {
            if (!StringUtils.hasText(request.getRecipientUserId())
                    || !StringUtils.hasText(request.getTitle())
                    || !StringUtils.hasText(request.getBody())) {
                responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("recipientUserId, title, and body are required")
                        .build());
                responseObserver.onCompleted();
                return;
            }
            CreateUserNotificationRequest payload = new CreateUserNotificationRequest();
            payload.setRecipientUserId(UUID.fromString(request.getRecipientUserId().trim()));
            payload.setTitle(request.getTitle());
            payload.setBody(request.getBody());
            payload.setTopic(blankToNull(request.getTopic()));
            payload.setReferenceType(blankToNull(request.getReferenceType()));
            payload.setReferenceId(blankToNull(request.getReferenceId()));
            payload.setNotificationType(blankToNull(request.getNotificationType()));
            payload.setCategory(blankToNull(request.getCategory()));
            payload.setRequestType(blankToNull(request.getRequestType()));
            payload.setRequestId(parseUuid(request.getRequestId()));
            payload.setRequestNumber(blankToNull(request.getRequestNumber()));
            payload.setWorkflowHistoryId(parseUuid(request.getWorkflowHistoryId()));
            payload.setActionUrl(blankToNull(request.getActionUrl()));
            payload.setActionLabel(blankToNull(request.getActionLabel()));
            payload.setCompanyId(parseUuid(request.getCompanyId()));
            payload.setBranchId(parseUuid(request.getBranchId()));
            payload.setRecipientEmployeeId(parseUuid(request.getRecipientEmployeeId()));
            payload.setChannel(blankToNull(request.getChannel()) != null ? request.getChannel() : "IN_APP");
            payload.setMetadata(blankToNull(request.getMetadata()));
            payload.setSendEmail(false);
            userNotificationService.create(payload);
            responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("In-app notification created")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Failed to create in-app notification via gRPC: {}", ex.getMessage(), ex);
            responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(ex.getMessage() == null ? "Failed to create notification" : ex.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void listUserNotifications(
            ListUserNotificationsGrpcRequest request,
            StreamObserver<ListUserNotificationsGrpcResponse> responseObserver) {
        try {
            UUID userId = parseUuid(request.getUserId());
            if (userId == null) {
                responseObserver.onNext(ListUserNotificationsGrpcResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("user_id is required")
                        .build());
                responseObserver.onCompleted();
                return;
            }
            UserNotificationListRequest listRequest = new UserNotificationListRequest();
            listRequest.setPageNo(request.getPageNo() > 0 ? request.getPageNo() : 0);
            listRequest.setPageSize(request.getPageSize() > 0 ? request.getPageSize() : 10);
            listRequest.setSortBy(
                    request.getSortBy() != null && !request.getSortBy().isBlank()
                            ? request.getSortBy().trim()
                            : "createdAt");
            listRequest.setDirection(
                    request.getSortDirection() != null && !request.getSortDirection().isBlank()
                            ? request.getSortDirection().trim()
                            : "desc");
            if (request.getReadFilter() == 1) {
                listRequest.setRead(Boolean.FALSE);
            } else if (request.getReadFilter() == 2) {
                listRequest.setRead(Boolean.TRUE);
            }
            if (request.getSearch() != null && !request.getSearch().isBlank()) {
                listRequest.setSearch(request.getSearch().trim());
            }
            UserNotificationListResponse result = request.getExcludeCompanyNotice()
                    ? userNotificationService.listWorkflowForUser(userId, listRequest)
                    : userNotificationService.listForUser(userId, listRequest);
            ListUserNotificationsGrpcResponse.Builder builder = ListUserNotificationsGrpcResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("ok")
                    .setTotalElements(result.getTotalElements())
                    .setUnreadCount(result.getUnreadCount());
            if (result.getResult() != null) {
                for (UserNotificationResponse item : result.getResult()) {
                    builder.addItems(toGrpcItem(item));
                }
            }
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Failed to list user notifications via gRPC: {}", ex.getMessage(), ex);
            responseObserver.onNext(ListUserNotificationsGrpcResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(ex.getMessage() == null ? "Failed to list notifications" : ex.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getUserNotificationUnreadCount(
            UserNotificationUserGrpcRequest request,
            StreamObserver<UserNotificationUnreadCountGrpcResponse> responseObserver) {
        try {
            UUID userId = parseUuid(request.getUserId());
            if (userId == null) {
                responseObserver.onNext(UserNotificationUnreadCountGrpcResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("user_id is required")
                        .build());
                responseObserver.onCompleted();
                return;
            }
            UserNotificationUnreadCountResponse count = request.getExcludeCompanyNotice()
                    ? userNotificationService.workflowUnreadCountForUser(userId)
                    : userNotificationService.unreadCountForUser(userId);
            responseObserver.onNext(UserNotificationUnreadCountGrpcResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("ok")
                    .setUnreadCount(count.getUnreadCount())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Failed to get unread count via gRPC: {}", ex.getMessage(), ex);
            responseObserver.onNext(UserNotificationUnreadCountGrpcResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(ex.getMessage() == null ? "Failed to get unread count" : ex.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void markUserNotificationRead(
            MarkUserNotificationReadGrpcRequest request,
            StreamObserver<SendTemplateEmailResponse> responseObserver) {
        try {
            UUID userId = parseUuid(request.getUserId());
            UUID notificationId = parseUuid(request.getNotificationId());
            if (userId == null || notificationId == null) {
                responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("user_id and notification_id are required")
                        .build());
                responseObserver.onCompleted();
                return;
            }
            userNotificationService.markReadForUser(userId, notificationId);
            responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Notification marked as read")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Failed to mark notification read via gRPC: {}", ex.getMessage(), ex);
            responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(ex.getMessage() == null ? "Failed to mark read" : ex.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void markAllUserNotificationsRead(
            UserNotificationUserGrpcRequest request,
            StreamObserver<SendTemplateEmailResponse> responseObserver) {
        try {
            UUID userId = parseUuid(request.getUserId());
            if (userId == null) {
                responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("user_id is required")
                        .build());
                responseObserver.onCompleted();
                return;
            }
            userNotificationService.markAllReadForUser(userId);
            responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("All notifications marked as read")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Failed to mark all notifications read via gRPC: {}", ex.getMessage(), ex);
            responseObserver.onNext(SendTemplateEmailResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(ex.getMessage() == null ? "Failed to mark all read" : ex.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    private static UserNotificationGrpcItem toGrpcItem(UserNotificationResponse item) {
        UserNotificationGrpcItem.Builder builder = UserNotificationGrpcItem.newBuilder()
                .setRead(item.isRead());
        if (item.getId() != null) {
            builder.setId(item.getId().toString());
        }
        if (StringUtils.hasText(item.getTitle())) {
            builder.setTitle(item.getTitle());
        }
        if (StringUtils.hasText(item.getBody())) {
            builder.setBody(item.getBody());
        }
        if (StringUtils.hasText(item.getTopic())) {
            builder.setTopic(item.getTopic());
        }
        if (StringUtils.hasText(item.getNotificationType())) {
            builder.setNotificationType(item.getNotificationType());
        }
        if (StringUtils.hasText(item.getCategory())) {
            builder.setCategory(item.getCategory());
        }
        if (StringUtils.hasText(item.getRequestType())) {
            builder.setRequestType(item.getRequestType());
        }
        if (item.getRequestId() != null) {
            builder.setRequestId(item.getRequestId().toString());
        }
        if (StringUtils.hasText(item.getActionUrl())) {
            builder.setActionUrl(item.getActionUrl());
        }
        if (item.getCreatedAt() != null) {
            builder.setCreatedAt(item.getCreatedAt().toString());
        }
        if (item.getReadAt() != null) {
            builder.setReadAt(item.getReadAt().toString());
        }
        if (StringUtils.hasText(item.getReferenceType())) {
            builder.setReferenceType(item.getReferenceType());
        }
        if (StringUtils.hasText(item.getReferenceId())) {
            builder.setReferenceId(item.getReferenceId());
        }
        if (StringUtils.hasText(item.getActionLabel())) {
            builder.setActionLabel(item.getActionLabel());
        }
        if (StringUtils.hasText(item.getMetadata())) {
            builder.setMetadata(item.getMetadata());
        }
        if (item.getRecipientEmployeeId() != null) {
            builder.setRecipientEmployeeId(item.getRecipientEmployeeId().toString());
        }
        return builder.build();
    }

    private static UUID resolveCompanyId(String companyIdField, Map<String, String> data) {
        UUID fromField = parseUuid(companyIdField);
        if (fromField != null) {
            return fromField;
        }
        if (data == null) {
            return null;
        }
        return parseUuid(data.get("COMPANY_ID"));
    }

    private static Long parseMobile(String mobile) {
        if (!StringUtils.hasText(mobile)) {
            return null;
        }
        String digits = mobile.replaceAll("\\D", "");
        if (!StringUtils.hasText(digits)) {
            return null;
        }
        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private static UUID parseUuid(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return UUID.fromString(value.trim());
        } catch (Exception ex) {
            return null;
        }
    }

    private static BulkSendRequest.TargetType mapTargetType(BulkTargetType targetType) {
        if (targetType == BulkTargetType.BULK_TARGET_ROLE) {
            return BulkSendRequest.TargetType.ROLE;
        }
        if (targetType == BulkTargetType.BULK_TARGET_SELECTED) {
            return BulkSendRequest.TargetType.SELECTED;
        }
        return BulkSendRequest.TargetType.ALL;
    }

    private TopicEnum parseTopic(String topic) {
        if (topic == null || topic.isBlank()) {
            return TopicEnum.REGISTER;
        }
        try {
            return TopicEnum.valueOf(topic.trim().toUpperCase());
        } catch (Exception ex) {
            return TopicEnum.REGISTER;
        }
    }

    private LanguageEnum parseLanguage(String language) {
        if (language == null || language.isBlank()) {
            return LanguageEnum.EN;
        }
        try {
            return LanguageEnum.valueOf(language.trim().toUpperCase());
        } catch (Exception ex) {
            return LanguageEnum.EN;
        }
    }
}
