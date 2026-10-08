package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.webclient.UserCommunication;
import com.nepbrain.technology.communication.webclient.UserContact;
import com.nepbrain.technology.communication.enums.LanguageEnum;
import com.nepbrain.technology.communication.payload.request.BulkSendRequest;
import com.nepbrain.technology.communication.payload.request.CommunicationRequest;
import com.nepbrain.technology.communication.payload.response.BulkSendResultResponse;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BulkSendService {

    private final UserCommunication userCommunication;
    private final CommunicationService communicationService;

    public GlobalResponse sendBulk(BulkSendRequest request) throws GlobalException {
        boolean fetchAll = request.getTargetType() == BulkSendRequest.TargetType.ALL;
        List<String> roleIds = request.getTargetType() == BulkSendRequest.TargetType.ROLE ? request.getRoleIds() : null;
        List<String> userIds = request.getTargetType() == BulkSendRequest.TargetType.SELECTED ? request.getUserIds() : null;

        List<UserContact> users = userCommunication.getUsersForBulkMessage(fetchAll, roleIds, userIds);

        int totalUsers = users.size();
        int emailSent = 0, emailFailed = 0, smsSent = 0, smsFailed = 0, notificationSent = 0, notificationFailed = 0;

        for (UserContact u : users) {
            Long mobile = null;
            if (u.getMobileNumber() != null && !u.getMobileNumber().isBlank()) {
                String digits = u.getMobileNumber().replaceAll("\\D", "");
                if (!digits.isEmpty()) {
                    try {
                        mobile = Long.parseLong(digits);
                    } catch (NumberFormatException ignored) { }
                }
            }
            CommunicationRequest comm = CommunicationRequest.builder()
                    .username(u.getFullName())
                    .email(u.getEmail())
                    .mobile(mobile)
                    .language(LanguageEnum.EN)
                    .tokens(u.getFcmTokens() != null ? u.getFcmTokens() : List.of())
                    .data(new HashMap<>())
                    .build();

            if (request.isSendEmail() && u.getEmail() != null && !u.getEmail().isBlank()) {
                try {
                    communicationService.sendWithExplicitContent(comm,
                            true, false, false,
                            request.getEmailSubject(), request.getEmailBody(), null, null, null);
                    emailSent++;
                } catch (Exception e) {
                    log.warn("Bulk email failed for {}: {}", u.getEmail(), e.getMessage());
                    emailFailed++;
                }
            }
            if (request.isSendSms() && u.getMobileNumber() != null && !u.getMobileNumber().isBlank() && comm.getMobile() != null) {
                try {
                    communicationService.sendWithExplicitContent(comm,
                            false, true, false,
                            null, null, request.getSmsBody(), null, null);
                    smsSent++;
                } catch (Exception e) {
                    log.warn("Bulk SMS failed for {}: {}", u.getMobileNumber(), e.getMessage());
                    smsFailed++;
                }
            }
            if (request.isSendNotification() && u.getFcmTokens() != null && !u.getFcmTokens().isEmpty()) {
                try {
                    communicationService.sendWithExplicitContent(comm,
                            false, false, true,
                            null, null, null, request.getNotificationTitle(), request.getNotificationBody());
                    notificationSent++;
                } catch (Exception e) {
                    log.warn("Bulk notification failed for {}: {}", u.getId(), e.getMessage());
                    notificationFailed++;
                }
            }
        }

        BulkSendResultResponse result = BulkSendResultResponse.builder()
                .totalUsers(totalUsers)
                .emailSent(emailSent)
                .emailFailed(emailFailed)
                .smsSent(smsSent)
                .smsFailed(smsFailed)
                .notificationSent(notificationSent)
                .notificationFailed(notificationFailed)
                .build();
        return GlobalResponseBuilder.buildSuccessResponse("message.bulk.sent.success", result);
    }
}
