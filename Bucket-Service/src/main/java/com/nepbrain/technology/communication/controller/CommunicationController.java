package com.nepbrain.technology.communication.controller;

import com.nepbrain.technology.communication.annotation.CommunicationRestController;
import com.nepbrain.technology.communication.enums.MessageModuleEnum;
import com.nepbrain.technology.communication.enums.PlatformEnum;
import com.nepbrain.technology.communication.payload.request.CommunicationRequest;
import com.nepbrain.technology.communication.payload.request.CommunicationSearchFilterPaginationRequest;
import com.nepbrain.technology.communication.payload.request.CreateUserNotificationRequest;
import com.nepbrain.technology.communication.payload.request.ExplicitEmailRequest;
import com.nepbrain.technology.communication.payload.request.OTPVerifyRequest;
import com.nepbrain.technology.communication.payload.request.UserNotificationListRequest;
import com.nepbrain.technology.communication.enums.ChannelEnum;
import com.nepbrain.technology.communication.enums.LanguageEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import com.nepbrain.technology.communication.service.CommunicationService;
import com.nepbrain.technology.communication.service.UserNotificationService;
import com.nepbrain.technology.communication.util.ReactiveBlocking;
import com.nepbrain.technology.core.config.security.CurrentUser;
import com.nepbrain.technology.communication.enums.ErrorCodeEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@CommunicationRestController
@RequiredArgsConstructor
@Tag(name = "Communication", description = "Send messages, OTP, communication logs, and user notifications")
public class CommunicationController {

    private final CommunicationService communicationService;
    private final UserNotificationService userNotificationService;

    @PostMapping("/send")
    @Operation(summary = "Send a templated message (email/SMS/notification)")
    public Mono<ResponseEntity<GlobalResponse>> sendMessage(@RequestBody CommunicationRequest request) {
        return ReactiveBlocking.call(() -> {
            communicationService.send(request);
            return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("Message sent successfully."));
        });
    }

    @PostMapping("/send/micro")
    @Operation(summary = "Send message (service-to-service, WEB platform)")
    public Mono<ResponseEntity<GlobalResponse>> sendMicroMessage(@RequestBody CommunicationRequest request) {
        return ReactiveBlocking.call(() -> {
            request.setPlatform(PlatformEnum.WEB);
            communicationService.send(request);
            return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("Message sent successfully."));
        });
    }

    @PostMapping("/send/explicit")
    @Operation(summary = "Send raw HTML email to an address (company notices / service-to-service)")
    public Mono<ResponseEntity<GlobalResponse>> sendExplicitEmail(@Valid @RequestBody ExplicitEmailRequest request) {
        return ReactiveBlocking.call(() -> {
            CommunicationRequest communicationRequest = CommunicationRequest.builder()
                    .username(request.getUsername() != null && !request.getUsername().isBlank()
                            ? request.getUsername()
                            : request.getEmail())
                    .email(request.getEmail().trim())
                    .platform(PlatformEnum.WEB)
                    .topic(TopicEnum.BULK)
                    .language(LanguageEnum.EN)
                    .sendTo(ChannelEnum.EMAIL)
                    .companyId(request.getCompanyId())
                    .build();
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
            return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("Email sent successfully."));
        });
    }

    @PostMapping("/generateOTP")
    @Operation(summary = "Generate OTP")
    public Mono<ResponseEntity<GlobalResponse>> generateOTP(@RequestBody CommunicationRequest request) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(communicationService.generateOTP(request)));
    }

    @PostMapping("/generateOTP/micro")
    @Operation(summary = "Generate OTP (microservice)")
    public Mono<ResponseEntity<GlobalResponse>> generateMicroOTP(@RequestBody CommunicationRequest request) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(communicationService.generateOTP(request)));
    }

    @PostMapping("/verifyOTP")
    @Operation(summary = "Verify OTP")
    public Mono<ResponseEntity<GlobalResponse>> verifyOTP(@RequestBody OTPVerifyRequest request) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(communicationService.verifyOTP(request)));
    }

    @PostMapping("/logs")
    @Operation(summary = "List communication logs")
    public Mono<ResponseEntity<GlobalResponse>> getCommunicationsLog(
            @RequestBody CommunicationSearchFilterPaginationRequest request) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(communicationService.getCommunicationsLog(request)));
    }

    @GetMapping("/topic")
    @Operation(summary = "List communication topics")
    public Mono<ResponseEntity<GlobalResponse>> getTopicEnums(
            @RequestParam(name = "module", required = false) MessageModuleEnum module) {
        return ReactiveBlocking.call(() -> ResponseEntity.ok(communicationService.getTopicEnums(module)));
    }

    @PostMapping("/notification/list")
    @Operation(summary = "List in-app notifications for the authenticated user")
    public Mono<ResponseEntity<GlobalResponse>> listNotifications(
            @RequestBody(required = false) UserNotificationListRequest request) {
        return withCurrentUser(userId -> ReactiveBlocking.call(() -> ResponseEntity.ok(
                GlobalResponseBuilder.buildSuccessResponse(
                        "Notifications retrieved successfully.",
                        userNotificationService.listForUser(userId, request)))));
    }

    @PostMapping("/notification/list-paginate")
    @Operation(summary = "List in-app notifications for the authenticated user (paginated)")
    public Mono<ResponseEntity<GlobalResponse>> listNotificationsPaginated(
            @RequestBody(required = false) UserNotificationListRequest request) {
        return withCurrentUser(userId -> ReactiveBlocking.call(() -> ResponseEntity.ok(
                GlobalResponseBuilder.buildSuccessResponse(
                        "Notifications retrieved successfully.",
                        userNotificationService.listForUser(userId, request)))));
    }

    @GetMapping("/notification/unread-count")
    @Operation(summary = "Unread in-app notification count for the authenticated user")
    public Mono<ResponseEntity<GlobalResponse>> notificationUnreadCount() {
        return withCurrentUser(userId -> ReactiveBlocking.call(() -> ResponseEntity.ok(
                GlobalResponseBuilder.buildSuccessResponse(
                        "Unread notification count retrieved successfully.",
                        userNotificationService.unreadCountForUser(userId)))));
    }

    @PatchMapping("/notification/mark-read")
    @Operation(summary = "Mark an in-app notification as read")
    public Mono<ResponseEntity<GlobalResponse>> markNotificationReadPatch(@RequestHeader UUID id) {
        return withCurrentUser(userId -> ReactiveBlocking.call(() -> {
            userNotificationService.markReadForUser(userId, id);
            return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("Notification marked as read."));
        }));
    }

    @PutMapping("/notification/mark-read")
    @Operation(summary = "Mark an in-app notification as read")
    public Mono<ResponseEntity<GlobalResponse>> markNotificationReadPut(@RequestHeader UUID id) {
        return withCurrentUser(userId -> ReactiveBlocking.call(() -> {
            userNotificationService.markReadForUser(userId, id);
            return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("Notification marked as read."));
        }));
    }

    @PostMapping("/notification/mark-all-read")
    @Operation(summary = "Mark all in-app notifications as read for the authenticated user")
    public Mono<ResponseEntity<GlobalResponse>> markAllNotificationsReadPost() {
        return withCurrentUser(userId -> ReactiveBlocking.call(() -> {
            userNotificationService.markAllReadForUser(userId);
            return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("All notifications marked as read."));
        }));
    }

    @PatchMapping("/notification/mark-all-read")
    @Operation(summary = "Mark all in-app notifications as read for the authenticated user")
    public Mono<ResponseEntity<GlobalResponse>> markAllNotificationsReadPatch() {
        return withCurrentUser(userId -> ReactiveBlocking.call(() -> {
            userNotificationService.markAllReadForUser(userId);
            return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("All notifications marked as read."));
        }));
    }

    @PostMapping("/notification/create")
    @Operation(summary = "Create an in-app notification for a portal user")
    public Mono<ResponseEntity<GlobalResponse>> createNotification(
            @Valid @RequestBody CreateUserNotificationRequest request) {
        return ReactiveBlocking.call(() -> {
            userNotificationService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(GlobalResponseBuilder.buildSuccessResponse("Notification created successfully."));
        });
    }

    private Mono<ResponseEntity<GlobalResponse>> withCurrentUser(
            Function<UUID, Mono<ResponseEntity<GlobalResponse>>> action) {
        return CurrentUser.getUserAsUuid()
                .switchIfEmpty(Mono.error(new GlobalException(
                        ErrorCodeEnum.NOT002.name(),
                        ErrorCodeEnum.NOT002.getValue(),
                        HttpStatus.UNAUTHORIZED)))
                .flatMap(action);
    }
}
