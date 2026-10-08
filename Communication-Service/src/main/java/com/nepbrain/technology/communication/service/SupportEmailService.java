package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.payload.request.SupportEmailDataRequest;
import com.nepbrain.technology.communication.payload.request.SupportEmailReplyRequest;
import com.nepbrain.technology.communication.payload.request.SupportEmailRequest;
import com.nepbrain.technology.core.payload.response.GlobalResponse;

import java.util.UUID;

public interface SupportEmailService {

    GlobalResponse create(SupportEmailRequest request);

    GlobalResponse addReply(UUID supportEmailId, SupportEmailReplyRequest request);

    GlobalResponse getById(UUID id);

    GlobalResponse getAll(SupportEmailDataRequest request);
}
