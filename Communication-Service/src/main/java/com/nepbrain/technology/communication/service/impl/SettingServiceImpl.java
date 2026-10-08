package com.nepbrain.technology.communication.service.impl;

import com.nepbrain.technology.communication.entity.SettingCategoryEntity;
import com.nepbrain.technology.communication.entity.SettingEntity;
import com.nepbrain.technology.communication.enums.ErrorCodeEnum;
import com.nepbrain.technology.communication.payload.request.SettingListRequest;
import com.nepbrain.technology.communication.payload.request.SettingRequest;
import com.nepbrain.technology.communication.payload.response.DatapaginationResponse;
import com.nepbrain.technology.communication.payload.response.SettingResponse;
import com.nepbrain.technology.communication.repository.SettingCategoryRepository;
import com.nepbrain.technology.communication.repository.SettingDetailRepository;
import com.nepbrain.technology.communication.repository.SettingRepository;
import com.nepbrain.technology.communication.service.SettingService;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {

    private final SettingRepository settingRepository;
    private final SettingCategoryRepository settingCategoryRepository;
    private final SettingDetailRepository settingDetailRepository;

    @Override
    @Transactional
    public GlobalResponse create(SettingRequest request) {
        if (settingRepository.findBySettingCode(request.getSettingCode()).isPresent()) {
            throw new GlobalException(ErrorCodeEnum.MSG001.name(), HttpStatus.CONFLICT);
        }
        SettingEntity entity = SettingEntity.builder()
                .settingCode(request.getSettingCode())
                .name(request.getName())
                .description(request.getDescription())
                .build();
        settingRepository.save(entity);
        return GlobalResponseBuilder.buildSuccessResponse("message.setting.create.success");
    }

    @Override
    @Transactional
    public GlobalResponse update(UUID id, SettingRequest request) {
        SettingEntity entity = settingRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET001.name()));
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        if (!entity.getSettingCode().equals(request.getSettingCode())) {
            if (settingRepository.findBySettingCode(request.getSettingCode()).isPresent()) {
                throw new GlobalException(ErrorCodeEnum.MSG001.name(), HttpStatus.CONFLICT);
            }
            entity.setSettingCode(request.getSettingCode());
        }
        settingRepository.save(entity);
        return GlobalResponseBuilder.buildSuccessResponse("message.setting.update.success");
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getById(UUID id) {
        SettingEntity entity = settingRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET001.name()));
        return GlobalResponseBuilder.buildSuccessResponse(toResponse(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse list(SettingListRequest request) {
        Specification<SettingEntity> spec = (root, query, cb) -> {
            if (!StringUtils.hasText(request.getSearchKey())) {
                return cb.conjunction();
            }
            String key = "%" + request.getSearchKey().trim().toLowerCase() + "%";
            Predicate nameLike = cb.like(cb.lower(root.get("name")), key);
            Predicate codeLike = cb.like(cb.lower(root.get("settingCode")), key);
            return cb.or(nameLike, codeLike);
        };
        String dir = request.getDirection() != null && "desc".equalsIgnoreCase(request.getDirection()) ? "desc" : "asc";
        String sortBy = StringUtils.hasText(request.getSortBy()) ? request.getSortBy() : "createdAt";
        int pageNo = Math.max(0, request.getPageNo());
        int pageSize = request.getPageSize() <= 0 ? 10 : request.getPageSize();
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(dir), sortBy));
        Page<SettingEntity> page = settingRepository.findAll(spec, pageable);
        List<SettingResponse> result = page.getContent().stream().map(this::toResponse).toList();
        return GlobalResponseBuilder.buildSuccessResponse(DatapaginationResponse.builder()
                .totalElementCount(page.getTotalElements())
                .result(result)
                .build());
    }

    @Override
    @Transactional
    public GlobalResponse delete(UUID id) {
        SettingEntity entity = settingRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET001.name()));
        List<SettingCategoryEntity> categories = settingCategoryRepository.findAllBySetting(entity);
        for (SettingCategoryEntity category : categories) {
            settingDetailRepository.findAllBySettingCategory(category).forEach(settingDetailRepository::delete);
            settingCategoryRepository.delete(category);
        }
        settingRepository.delete(entity);
        return GlobalResponseBuilder.buildSuccessResponse("message.setting.delete.success");
    }

    private SettingResponse toResponse(SettingEntity entity) {
        return SettingResponse.builder()
                .id(entity.getId())
                .settingCode(entity.getSettingCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .lastModifiedAt(entity.getLastModifiedAt())
                .build();
    }
}
