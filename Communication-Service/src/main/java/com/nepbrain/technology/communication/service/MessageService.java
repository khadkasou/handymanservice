package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.enums.StatusEnum;
import com.nepbrain.technology.communication.payload.request.MessageDataRequest;
import com.nepbrain.technology.communication.payload.request.MessageDetailRequest;
import com.nepbrain.technology.communication.payload.request.MessageRequest;
import com.nepbrain.technology.core.payload.response.GlobalResponse;

import java.util.UUID;

/*
 * @Created At 03/03/2024
 * @Author suroj.awal
 */
public interface MessageService {

    GlobalResponse create(MessageRequest request);

    GlobalResponse update(MessageRequest request);

    GlobalResponse getById(UUID id);

    GlobalResponse getAll(MessageDataRequest request);

    GlobalResponse delete(UUID id);

    GlobalResponse changeStatus(UUID id, StatusEnum status);

    GlobalResponse addDetail(UUID messageId, MessageDetailRequest request);

    GlobalResponse updateDetail(UUID detailId, MessageDetailRequest request);
}
