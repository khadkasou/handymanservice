package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.enums.MessageModuleEnum;
import com.nepbrain.technology.communication.payload.request.CommunicationRequest;
import com.nepbrain.technology.communication.payload.request.CommunicationSearchFilterPaginationRequest;
import com.nepbrain.technology.communication.payload.request.OTPVerifyRequest;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;

public interface CommunicationService {
    GlobalResponse send(CommunicationRequest request) throws GlobalException;

    /**
     * Send email/SMS/notification with explicit content (no topic template).
     * Only sends channels where the corresponding content string is non-null and the flag is true.
     */
    void sendWithExplicitContent(CommunicationRequest request,
                                 boolean sendEmail, boolean sendSms, boolean sendNotification,
                                 String emailSubject, String emailBody, String smsBody,
                                 String notificationTitle, String notificationBody) throws GlobalException;

    GlobalResponse generateOTP(CommunicationRequest request) throws GlobalException;

    GlobalResponse verifyOTP(OTPVerifyRequest request) throws GlobalException;

    GlobalResponse getCommunicationsLog(CommunicationSearchFilterPaginationRequest request);

    GlobalResponse getTopicEnums();

    GlobalResponse getTopicEnums(MessageModuleEnum module);
}
