package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.enums.StatusEnum;
import com.nepbrain.technology.communication.payload.request.MessageDataRequest;
import com.nepbrain.technology.communication.payload.request.MessageDetailRequest;
import com.nepbrain.technology.communication.payload.request.MessageRequest;
import com.nepbrain.technology.communication.payload.response.DatapaginationResponse;
import com.nepbrain.technology.communication.payload.response.MessageDetailResponse;
import com.nepbrain.technology.communication.payload.response.MessageResponse;

import java.util.UUID;

/*
 * @Created At 03/03/2024
 * @Author suroj.awal
 */
public interface MessageService {

    void create(MessageRequest request);

    void update(MessageRequest request);

    MessageResponse getById(UUID id);

    DatapaginationResponse getAll(MessageDataRequest request);

    void delete(UUID id);

    void changeStatus(UUID id, StatusEnum status);

    MessageDetailResponse addDetail(UUID messageId, MessageDetailRequest request);

    MessageDetailResponse updateDetail(UUID detailId, MessageDetailRequest request);
}
