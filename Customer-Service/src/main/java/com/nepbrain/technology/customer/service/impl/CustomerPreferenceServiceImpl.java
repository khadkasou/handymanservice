package com.nepbrain.technology.customer.service.impl;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import com.nepbrain.technology.customer.entity.CustomerPreferenceEntity;
import com.nepbrain.technology.customer.entity.CustomerEntity;
import com.nepbrain.technology.customer.enums.ErrorCodeEnum;
import com.nepbrain.technology.customer.mapper.CustomerPreferenceMapper;
import com.nepbrain.technology.customer.payload.request.CustomerPreferenceRequest;
import com.nepbrain.technology.customer.payload.response.CustomerPreferenceResponse;
import com.nepbrain.technology.customer.repository.CustomerPreferenceRepository;
import com.nepbrain.technology.customer.repository.CustomerRepository;
import com.nepbrain.technology.customer.service.CustomerPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerPreferenceServiceImpl implements CustomerPreferenceService {

    private final CustomerPreferenceRepository preferenceRepository;
    private final CustomerRepository customerRepository;
    private final CustomerPreferenceMapper preferenceMapper;

    @Override
    public GlobalResponse create(CustomerPreferenceRequest request) {
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUS002.name()));

        if (preferenceRepository.existsByCustomerIdAndPreferenceKey(request.getCustomerId(), request.getPreferenceKey())) {
            throw new GlobalException(ErrorCodeEnum.CUSP002.name(), HttpStatus.CONFLICT);
        }

        CustomerPreferenceEntity entity = preferenceMapper.toEntity(request);
        entity.setCustomer(customer);
        if (entity.getStatus() == null) {
            entity.setStatus(StatusEnum.ACTIVE);
        }
        CustomerPreferenceEntity saved = preferenceRepository.save(entity);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.preference.create.success", preferenceMapper.toResponse(saved));
    }

    @Override
    public GlobalResponse update(UUID id, CustomerPreferenceRequest request) {
        CustomerPreferenceEntity entity = preferenceRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSP001.name()));

        if (!entity.getCustomer().getId().equals(request.getCustomerId())) {
            CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUS002.name()));
            entity.setCustomer(customer);
        }

        if (!entity.getPreferenceKey().equals(request.getPreferenceKey()) &&
            preferenceRepository.existsByCustomerIdAndPreferenceKey(request.getCustomerId(), request.getPreferenceKey())) {
            throw new GlobalException(ErrorCodeEnum.CUSP002.name(), HttpStatus.CONFLICT);
        }

        preferenceMapper.updateEntity(request, entity);
        CustomerPreferenceEntity saved = preferenceRepository.save(entity);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.preference.update.success", preferenceMapper.toResponse(saved));
    }

    @Override
    public GlobalResponse upsert(CustomerPreferenceRequest request) {
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUS002.name()));

        Optional<CustomerPreferenceEntity> existing = preferenceRepository.findByCustomerIdAndPreferenceKey(
                request.getCustomerId(), request.getPreferenceKey());

        CustomerPreferenceEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
            preferenceMapper.updateEntity(request, entity);
        } else {
            entity = preferenceMapper.toEntity(request);
            entity.setCustomer(customer);
            entity.setStatus(StatusEnum.ACTIVE);
        }

        CustomerPreferenceEntity saved = preferenceRepository.save(entity);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.preference.upsert.success", preferenceMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getById(UUID id) {
        CustomerPreferenceEntity entity = preferenceRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSP001.name()));
        return GlobalResponseBuilder.buildSuccessResponse(preferenceMapper.toResponse(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getByCustomerIdAndPreferenceKey(UUID customerId, String preferenceKey) {
        CustomerPreferenceEntity entity = preferenceRepository.findByCustomerIdAndPreferenceKey(customerId, preferenceKey)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSP001.name()));
        return GlobalResponseBuilder.buildSuccessResponse(preferenceMapper.toResponse(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getByCustomerId(UUID customerId) {
        List<CustomerPreferenceResponse> preferences = preferenceRepository.findByCustomerId(customerId).stream()
                .map(preferenceMapper::toResponse)
                .collect(Collectors.toList());
        return GlobalResponseBuilder.buildSuccessResponse(preferences);
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getByCustomerIdAndCategory(UUID customerId, String category) {
        List<CustomerPreferenceResponse> preferences = preferenceRepository.findByCustomerIdAndCategory(customerId, category).stream()
                .map(preferenceMapper::toResponse)
                .collect(Collectors.toList());
        return GlobalResponseBuilder.buildSuccessResponse(preferences);
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getPreferencesAsMap(UUID customerId) {
        List<CustomerPreferenceEntity> entities = preferenceRepository.findByCustomerId(customerId);
        Map<String, String> preferencesMap = new HashMap<>();
        entities.forEach(pref -> preferencesMap.put(pref.getPreferenceKey(), pref.getPreferenceValue()));
        return GlobalResponseBuilder.buildSuccessResponse(preferencesMap);
    }

    @Override
    public GlobalResponse changeStatus(UUID id, StatusEnum status) {
        requireActiveOrInactive(status);
        CustomerPreferenceEntity entity = preferenceRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSP001.name(), HttpStatus.BAD_REQUEST));
        entity.setStatus(status);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.preference.status.success", preferenceMapper.toResponse(preferenceRepository.save(entity)));
    }

    @Override
    public GlobalResponse changeStatusByCustomerIdAndPreferenceKey(UUID customerId, String preferenceKey, StatusEnum status) {
        requireActiveOrInactive(status);
        CustomerPreferenceEntity entity = preferenceRepository.findByCustomerIdAndPreferenceKey(customerId, preferenceKey)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSP001.name(), HttpStatus.BAD_REQUEST));
        entity.setStatus(status);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.preference.status.success", preferenceMapper.toResponse(preferenceRepository.save(entity)));
    }

    @Override
    public GlobalResponse changeStatusByCustomerId(UUID customerId, StatusEnum status) {
        requireActiveOrInactive(status);
        List<CustomerPreferenceEntity> entities = preferenceRepository.findByCustomerId(customerId);
        List<CustomerPreferenceResponse> responses = new ArrayList<>();
        for (CustomerPreferenceEntity entity : entities) {
            entity.setStatus(status);
            responses.add(preferenceMapper.toResponse(preferenceRepository.save(entity)));
        }
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.preference.status.success", responses);
    }

    private void requireActiveOrInactive(StatusEnum status) {
        if (status != StatusEnum.ACTIVE && status != StatusEnum.INACTIVE) {
            throw new GlobalException(ErrorCodeEnum.CUSP003.name());
        }
    }
}
