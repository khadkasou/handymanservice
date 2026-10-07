package com.nepbrain.technology.communication.service.impl;

import com.nepbrain.technology.communication.entity.SettingCategoryEntity;
import com.nepbrain.technology.communication.entity.SettingEntity;
import com.nepbrain.technology.communication.enums.ErrorCodeEnum;
import com.nepbrain.technology.communication.payload.request.SettingCategoryListRequest;
import com.nepbrain.technology.communication.payload.request.SettingCategoryRequest;
import com.nepbrain.technology.communication.payload.response.DatapaginationResponse;
import com.nepbrain.technology.communication.payload.response.SettingCategoryResponse;
import com.nepbrain.technology.communication.repository.SettingCategoryRepository;
import com.nepbrain.technology.communication.repository.SettingDetailRepository;
import com.nepbrain.technology.communication.repository.SettingRepository;
import com.nepbrain.technology.communication.service.SettingCategoryService;
import com.nepbrain.technology.core.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettingCategoryServiceImpl implements SettingCategoryService {

    private final SettingCategoryRepository settingCategoryRepository;
    private final SettingRepository settingRepository;
    private final SettingDetailRepository settingDetailRepository;

    @Override
    @Transactional
    public void create(SettingCategoryRequest request) {
        SettingEntity setting = settingRepository.findById(request.getSettingId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET001.name()));
        if (settingCategoryRepository.findBySettingAndCategoryCode(setting, request.getCategoryCode()).isPresent()) {
            throw new GlobalException(ErrorCodeEnum.MSG001.name());
        }
        SettingCategoryEntity entity = SettingCategoryEntity.builder()
                .categoryCode(request.getCategoryCode())
                .name(request.getName())
                .description(request.getDescription())
                .setting(setting)
                .build();
        settingCategoryRepository.save(entity);
    }

    @Override
    @Transactional
    public void update(UUID id, SettingCategoryRequest request) {
        SettingCategoryEntity entity = settingCategoryRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET002.name()));
        SettingEntity setting = settingRepository.findById(request.getSettingId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET001.name()));
        if (!entity.getCategoryCode().equals(request.getCategoryCode())
                && settingCategoryRepository.findBySettingAndCategoryCode(setting, request.getCategoryCode()).isPresent()) {
            throw new GlobalException(ErrorCodeEnum.MSG001.name());
        }
        entity.setCategoryCode(request.getCategoryCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setSetting(setting);
        settingCategoryRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public SettingCategoryResponse getById(UUID id) {
        SettingCategoryEntity entity = settingCategoryRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET002.name()));
        return toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public DatapaginationResponse list(SettingCategoryListRequest request) {
        Specification<SettingCategoryEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request.getSettingId() != null) {
                predicates.add(cb.equal(root.get("setting").get("id"), request.getSettingId()));
            }
            if (StringUtils.hasText(request.getSearchKey())) {
                String key = "%" + request.getSearchKey().trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), key);
                Predicate codeLike = cb.like(cb.lower(root.get("categoryCode")), key);
                predicates.add(cb.or(nameLike, codeLike));
            }
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
        String dir = request.getDirection() != null && "desc".equalsIgnoreCase(request.getDirection()) ? "desc" : "asc";
        String sortBy = StringUtils.hasText(request.getSortBy()) ? request.getSortBy() : "createdAt";
        int pageNo = Math.max(0, request.getPageNo());
        int pageSize = request.getPageSize() <= 0 ? 10 : request.getPageSize();
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(dir), sortBy));
        Page<SettingCategoryEntity> page = settingCategoryRepository.findAll(spec, pageable);
        List<SettingCategoryResponse> result = page.getContent().stream().map(this::toResponse).toList();
        return DatapaginationResponse.builder()
                .totalElementCount(page.getTotalElements())
                .result(result)
                .build();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        SettingCategoryEntity entity = settingCategoryRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.SET002.name()));
        settingDetailRepository.findAllBySettingCategory(entity).forEach(settingDetailRepository::delete);
        settingCategoryRepository.delete(entity);
    }

    private SettingCategoryResponse toResponse(SettingCategoryEntity entity) {
        return SettingCategoryResponse.builder()
                .id(entity.getId())
                .categoryCode(entity.getCategoryCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .settingId(entity.getSetting() != null ? entity.getSetting().getId() : null)
                .settingCode(entity.getSetting() != null ? entity.getSetting().getSettingCode() : null)
                .createdAt(entity.getCreatedAt())
                .lastModifiedAt(entity.getLastModifiedAt())
                .build();
    }
}
