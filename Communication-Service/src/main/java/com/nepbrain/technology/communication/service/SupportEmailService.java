package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.payload.request.SupportEmailDataRequest;
import com.nepbrain.technology.communication.payload.request.SupportEmailReplyRequest;
import com.nepbrain.technology.communication.payload.request.SupportEmailRequest;
import com.nepbrain.technology.communication.payload.response.DatapaginationResponse;
import com.nepbrain.technology.communication.payload.response.SupportEmailReplyResponse;
import com.nepbrain.technology.communication.payload.response.SupportEmailResponse;

import java.util.UUID;

public interface SupportEmailService {

    SupportEmailResponse create(SupportEmailRequest request);

    SupportEmailReplyResponse addReply(UUID supportEmailId, SupportEmailReplyRequest request);

    SupportEmailResponse getById(UUID id);

    DatapaginationResponse getAll(SupportEmailDataRequest request);
}
