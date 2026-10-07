package com.nepbrain.technology.communication.service.impl;

import com.nepbrain.technology.communication.entity.SettingCategoryEntity;
import com.nepbrain.technology.communication.entity.SettingDetailEntity;
import com.nepbrain.technology.communication.enums.ErrorCodeEnum;
import com.nepbrain.technology.communication.payload.request.SettingDetailBulkUpdateRequest;
import com.nepbrain.technology.communication.payload.request.SettingDetailItemRequest;
import com.nepbrain.technology.communication.payload.request.SettingDetailListRequest;
import com.nepbrain.technology.communication.payload.request.SettingDetailRequest;
import com.nepbrain.technology.communication.payload.response.DatapaginationResponse;
import com.nepbrain.technology.communication.payload.response.SettingDetailResponse;
import com.nepbrain.technology.communication.repository.SettingCategoryRepository;
import com.nepbrain.technology.communication.repository.SettingDetailRepository;
import com.nepbrain.technology.communication.service.SettingDetailService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettingDetailServiceImpl implements SettingDetailService {

    private final SettingDetailRepository settingDetailRepository;
    private final SettingCategoryRepository settingCategoryRepository;

    @Override
    @Transactional
    public void create(SettingDetailRequest request) {
        SettingCategoryEntity category = settingCategoryRepository.findById(request.getSettingCategoryId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET002.name()));
        if (settingDetailRepository.findBySettingCategoryAndKey(category, request.getKey()).isPresent()) {
            throw new GlobalException(ErrorCodeEnum.MSG001.name());
        }
        SettingDetailEntity entity = SettingDetailEntity.builder()
                .key(request.getKey())
                .value(request.getValue())
                .displayName(request.getDisplayName())
                .settingCategory(category)
                .build();
        settingDetailRepository.save(entity);
    }

    @Override
    @Transactional
    public void update(UUID id, SettingDetailRequest request) {
        SettingDetailEntity entity = settingDetailRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET003.name()));
        SettingCategoryEntity category = settingCategoryRepository.findById(request.getSettingCategoryId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET002.name()));
        if (!entity.getKey().equals(request.getKey())
                && settingDetailRepository.findBySettingCategoryAndKey(category, request.getKey()).isPresent()) {
            throw new GlobalException(ErrorCodeEnum.MSG001.name());
        }
        entity.setKey(request.getKey());
        entity.setValue(request.getValue());
        entity.setDisplayName(request.getDisplayName());
        entity.setSettingCategory(category);
        settingDetailRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public SettingDetailResponse getById(UUID id) {
        SettingDetailEntity entity = settingDetailRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET003.name()));
        return toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public SettingDetailResponse getByKey(String key) {
        if (!StringUtils.hasText(key)) {
            throw new GlobalException(ErrorCodeEnum.SET003.name());
        }
        SettingDetailEntity entity = settingDetailRepository.findFirstByKeyIgnoreCase(key.trim())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET003.name()));
        return toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public DatapaginationResponse list(SettingDetailListRequest request) {
        Specification<SettingDetailEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request.getSettingCategoryId() != null) {
                predicates.add(cb.equal(root.get("settingCategory").get("id"), request.getSettingCategoryId()));
            }
            if (StringUtils.hasText(request.getSearchKey())) {
                String key = "%" + request.getSearchKey().trim().toLowerCase() + "%";
                Predicate keyLike = cb.like(cb.lower(root.get("key")), key);
                Predicate displayLike = cb.like(cb.lower(root.get("displayName")), key);
                predicates.add(cb.or(keyLike, displayLike));
            }
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
        String dir = request.getDirection() != null && "desc".equalsIgnoreCase(request.getDirection()) ? "desc" : "asc";
        String sortBy = StringUtils.hasText(request.getSortBy()) ? request.getSortBy() : "createdAt";
        int pageNo = Math.max(0, request.getPageNo());
        int pageSize = request.getPageSize() <= 0 ? 10 : request.getPageSize();
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(dir), sortBy));
        Page<SettingDetailEntity> page = settingDetailRepository.findAll(spec, pageable);
        List<SettingDetailResponse> result = page.getContent().stream().map(this::toResponse).toList();
        return DatapaginationResponse.builder()
                .totalElementCount(page.getTotalElements())
                .result(result)
                .build();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        SettingDetailEntity entity = settingDetailRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET003.name()));
        settingDetailRepository.delete(entity);
    }

    @Override
    @Transactional
    public void bulkUpdateBySettingCategoryId(SettingDetailBulkUpdateRequest request) {
        SettingCategoryEntity category = settingCategoryRepository.findById(request.getSettingCategoryId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET002.name()));
        for (SettingDetailItemRequest item : request.getDetails()) {
            settingDetailRepository.findBySettingCategoryAndKey(category, item.getKey())
                    .ifPresent(entity -> {
                        if (item.getValue() != null) {
                            entity.setValue(item.getValue());
                        }
                        if (item.getDisplayName() != null) {
                            entity.setDisplayName(item.getDisplayName());
                        }
                        settingDetailRepository.save(entity);
                    });
        }
    }

    private SettingDetailResponse toResponse(SettingDetailEntity entity) {
        return SettingDetailResponse.builder()
                .id(entity.getId())
                .key(entity.getKey())
                .value(entity.getValue())
                .displayName(entity.getDisplayName())
                .settingCategoryId(entity.getSettingCategory() != null ? entity.getSettingCategory().getId() : null)
                .settingCategoryCode(entity.getSettingCategory() != null ? entity.getSettingCategory().getCategoryCode() : null)
                .createdAt(entity.getCreatedAt())
                .lastModifiedAt(entity.getLastModifiedAt())
                .build();
    }
}
