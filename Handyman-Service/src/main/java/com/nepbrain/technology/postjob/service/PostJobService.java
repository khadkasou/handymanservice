package com.nepbrain.technology.postjob.service;
import com.nepbrain.technology.postjob.payload.request.PostJobRequest;
import com.nepbrain.technology.postjob.payload.response.PostJobResponse;
import java.util.List; import java.util.UUID;
public interface PostJobService {
    PostJobResponse savePostJob(PostJobRequest request);
    List<PostJobResponse> getPostJob(UUID customerId);
    PostJobResponse getPostJobDetail(UUID id);
    void postJobDelete(UUID id);
}
