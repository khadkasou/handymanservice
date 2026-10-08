package com.nepbrain.technology.postjob.service;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.postjob.payload.request.PostJobRequest;

import java.util.UUID;

public interface PostJobService {

    GlobalResponse savePostJob(PostJobRequest request);

    GlobalResponse getPostJob(UUID customerId);

    GlobalResponse getPostJobDetail(UUID id);

    GlobalResponse changePostJobStatus(UUID id, StatusEnum status);

    GlobalResponse postJobDelete(UUID id);
}
