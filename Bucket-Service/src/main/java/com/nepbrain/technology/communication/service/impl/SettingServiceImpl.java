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
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    public void create(SettingRequest request) {
        if (settingRepository.findBySettingCode(request.getSettingCode()).isPresent()) {
            throw new GlobalException(ErrorCodeEnum.MSG001.name());
        }
        SettingEntity entity = SettingEntity.builder()
                .settingCode(request.getSettingCode())
                .name(request.getName())
                .description(request.getDescription())
                .build();
        settingRepository.save(entity);
    }

    @Override
    @Transactional
    public void update(UUID id, SettingRequest request) {
        SettingEntity entity = settingRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET001.name()));
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        if (!entity.getSettingCode().equals(request.getSettingCode())) {
            if (settingRepository.findBySettingCode(request.getSettingCode()).isPresent()) {
                throw new GlobalException(ErrorCodeEnum.MSG001.name());
            }
            entity.setSettingCode(request.getSettingCode());
        }
        settingRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public SettingResponse getById(UUID id) {
        SettingEntity entity = settingRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET001.name()));
        return toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public DatapaginationResponse list(SettingListRequest request) {
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
        return DatapaginationResponse.builder()
                .totalElementCount(page.getTotalElements())
                .result(result)
                .build();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        SettingEntity entity = settingRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET001.name()));
        List<SettingCategoryEntity> categories = settingCategoryRepository.findAllBySetting(entity);
        for (SettingCategoryEntity category : categories) {
            settingDetailRepository.findAllBySettingCategory(category).forEach(settingDetailRepository::delete);
            settingCategoryRepository.delete(category);
        }
        settingRepository.delete(entity);
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
