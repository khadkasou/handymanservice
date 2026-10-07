package com.nepbrain.technology.communication.service.impl;

import com.nepbrain.technology.communication.entity.SupportEmailEntity;
import com.nepbrain.technology.communication.entity.SupportEmailReplyEntity;
import com.nepbrain.technology.communication.enums.ErrorCodeEnum;
import com.nepbrain.technology.communication.payload.request.SupportEmailDataRequest;
import com.nepbrain.technology.communication.payload.request.SupportEmailReplyRequest;
import com.nepbrain.technology.communication.payload.request.SupportEmailRequest;
import com.nepbrain.technology.communication.payload.response.DatapaginationResponse;
import com.nepbrain.technology.communication.payload.response.SupportEmailReplyResponse;
import com.nepbrain.technology.communication.payload.response.SupportEmailResponse;
import com.nepbrain.technology.communication.repository.SupportEmailReplyRepository;
import com.nepbrain.technology.communication.repository.SupportEmailRepository;
import com.nepbrain.technology.communication.service.SupportEmailService;
import com.nepbrain.technology.communication.service.specification.SupportEmailSpecification;
import com.nepbrain.technology.core.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportEmailServiceImpl implements SupportEmailService {

    private final SupportEmailRepository supportEmailRepository;
    private final SupportEmailReplyRepository supportEmailReplyRepository;

    @Override
    @Transactional
    public SupportEmailResponse create(SupportEmailRequest request) {
        SupportEmailEntity entity = SupportEmailEntity.builder()
                .email(request.getEmail())
                .name(request.getName())
                .mobileNumber(request.getMobileNumber())
                .isCompany(request.getIsCompany() != null ? request.getIsCompany() : false)
                .companyName(request.getCompanyName())
                .address(request.getAddress())
                .subject(request.getSubject())
                .message(request.getMessage())
                .build();
        entity = supportEmailRepository.save(entity);
        return toResponse(entity);
    }

    @Override
    @Transactional
    public SupportEmailReplyResponse addReply(UUID supportEmailId, SupportEmailReplyRequest request) {
        SupportEmailEntity supportEmail = supportEmailRepository.findById(supportEmailId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SUP001.name()));
        SupportEmailReplyEntity reply = SupportEmailReplyEntity.builder()
                .supportEmail(supportEmail)
                .message(request.getMessage())
                .build();
        reply = supportEmailReplyRepository.save(reply);
        return toReplyResponse(reply);
    }

    @Override
    public SupportEmailResponse getById(UUID id) {
        SupportEmailEntity entity = supportEmailRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SUP001.name()));
        List<SupportEmailReplyEntity> replies = supportEmailReplyRepository.findAllBySupportEmailOrderByCreatedAtAsc(entity);
        return toResponse(entity, replies);
    }

    @Override
    public DatapaginationResponse getAll(SupportEmailDataRequest request) {
        Specification<SupportEmailEntity> spec = SupportEmailSpecification.supportEmailFilter(request);
        String direction = request.getDirection() != null && request.getDirection().equalsIgnoreCase("desc") ? "desc" : "asc";
        String sortBy = request.getSortBy() != null && !request.getSortBy().isBlank() ? request.getSortBy() : "createdAt";
        int pageNo = Math.max(0, request.getPageNo());
        int pageSize = request.getPageSize() <= 0 ? 10 : request.getPageSize();
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<SupportEmailEntity> page = supportEmailRepository.findAll(spec, pageable);
        List<SupportEmailResponse> result = page.getContent().stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
        return DatapaginationResponse.builder()
                .totalElementCount(page.getTotalElements())
                .result(result)
                .build();
    }

    private SupportEmailResponse toSummaryResponse(SupportEmailEntity entity) {
        return toResponse(entity, Collections.emptyList());
    }

    private SupportEmailResponse toResponse(SupportEmailEntity entity) {
        List<SupportEmailReplyEntity> replies = supportEmailReplyRepository.findAllBySupportEmailOrderByCreatedAtAsc(entity);
        return toResponse(entity, replies);
    }

    private SupportEmailResponse toResponse(SupportEmailEntity entity, List<SupportEmailReplyEntity> replies) {
        return SupportEmailResponse.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .name(entity.getName())
                .mobileNumber(entity.getMobileNumber())
                .isCompany(entity.getIsCompany())
                .companyName(entity.getCompanyName())
                .address(entity.getAddress())
                .subject(entity.getSubject())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .replies(replies.stream().map(this::toReplyResponse).collect(Collectors.toList()))
                .build();
    }

    private SupportEmailReplyResponse toReplyResponse(SupportEmailReplyEntity entity) {
        return SupportEmailReplyResponse.builder()
                .id(entity.getId())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
