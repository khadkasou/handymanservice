package com.nepbrain.technology.postjob.service.impl;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import com.nepbrain.technology.postjob.entity.PostJobEntity;
import com.nepbrain.technology.postjob.enums.ErrorCodeEnum;
import com.nepbrain.technology.postjob.payload.request.PostJobRequest;
import com.nepbrain.technology.postjob.payload.response.PostJobResponse;
import com.nepbrain.technology.postjob.repository.PostJobRepository;
import com.nepbrain.technology.postjob.service.PostJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostJobServiceImpl implements PostJobService {

    private final PostJobRepository postJobRepository;

    @Override
    public GlobalResponse savePostJob(PostJobRequest request) {
        PostJobEntity job;
        if (request.getId() != null) {
            job = postJobRepository.findById(request.getId())
                    .orElseThrow(() -> new GlobalException(ErrorCodeEnum.PJB001.name()));
        } else {
            job = new PostJobEntity();
            job.setStatus("pending");
        }

        if (request.getTitle() != null) {
            job.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            job.setDescription(request.getDescription());
        }
        if (request.getCustomerId() != null) {
            job.setCustomerId(request.getCustomerId());
        }
        if (request.getPrice() != null) {
            job.setPrice(request.getPrice());
        }
        if (request.getAddress() != null) {
            job.setAddress(request.getAddress());
        }
        if (request.getCategoryId() != null) {
            job.setCategoryId(request.getCategoryId());
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            job.setStatus(normalizeStatus(request.getStatus()));
        }
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.postjob.save.success", toResponse(postJobRepository.save(job)));
    }

    @Override
    public GlobalResponse getPostJob(UUID customerId) {
        List<PostJobEntity> jobs;
        if (customerId != null) {
            jobs = postJobRepository.findByCustomerId(customerId);
        } else {
            jobs = postJobRepository.findAll();
        }

        List<PostJobResponse> result = new ArrayList<>();
        for (PostJobEntity job : jobs) {
            if (job.getStatus() != null && StatusEnum.INACTIVE.name().equalsIgnoreCase(job.getStatus())) {
                continue;
            }
            result.add(toResponse(job));
        }
        return GlobalResponseBuilder.buildSuccessResponse(result);
    }

    @Override
    public GlobalResponse getPostJobDetail(UUID id) {
        PostJobEntity job = postJobRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.PJB001.name()));
        return GlobalResponseBuilder.buildSuccessResponse(toResponse(job));
    }

    @Override
    public GlobalResponse changePostJobStatus(UUID id, StatusEnum status) {
        if (status != StatusEnum.ACTIVE && status != StatusEnum.INACTIVE) {
            throw new GlobalException(ErrorCodeEnum.PJB002.name());
        }
        PostJobEntity job = postJobRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.PJB001.name(), HttpStatus.BAD_REQUEST));
        job.setStatus(status.name());
        return GlobalResponseBuilder.buildSuccessResponse(
                "message.postjob.status.success", toResponse(postJobRepository.save(job)));
    }

    @Override
    public GlobalResponse postJobDelete(UUID id) {
        return changePostJobStatus(id, StatusEnum.INACTIVE);
    }

    private PostJobResponse toResponse(PostJobEntity job) {
        return PostJobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .customerId(job.getCustomerId())
                .status(job.getStatus())
                .price(job.getPrice())
                .address(job.getAddress())
                .categoryId(job.getCategoryId())
                .build();
    }

    private String normalizeStatus(String raw) {
        String value = raw.trim();
        try {
            return StatusEnum.valueOf(value.toUpperCase()).name();
        } catch (IllegalArgumentException ex) {
            return value.toLowerCase();
        }
    }
}
