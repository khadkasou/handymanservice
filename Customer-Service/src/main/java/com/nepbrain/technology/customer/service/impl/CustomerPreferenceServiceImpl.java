package com.nepbrain.technology.customer.service.impl;

import com.nepbrain.technology.core.exception.GlobalException;
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
    public CustomerPreferenceResponse create(CustomerPreferenceRequest request) {
        // Validate customer exists
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.ERR002.name(), HttpStatus.NOT_FOUND));

        // Check if preference already exists
        if (preferenceRepository.existsByCustomerIdAndPreferenceKey(request.getCustomerId(), request.getPreferenceKey())) {
            throw new GlobalException("PREFERENCE_KEY_EXISTS", HttpStatus.BAD_REQUEST);
        }

        CustomerPreferenceEntity entity = preferenceMapper.toEntity(request);
        entity.setCustomer(customer);
        CustomerPreferenceEntity saved = preferenceRepository.save(entity);
        return preferenceMapper.toResponse(saved);
    }

    @Override
    public CustomerPreferenceResponse update(UUID id, CustomerPreferenceRequest request) {
        CustomerPreferenceEntity entity = preferenceRepository.findById(id)
                .orElseThrow(() -> new GlobalException("PREFERENCE_NOT_FOUND", HttpStatus.NOT_FOUND));

        // Validate customer exists if changed
        if (!entity.getCustomer().getId().equals(request.getCustomerId())) {
            CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new GlobalException(ErrorCodeEnum.ERR002.name(), HttpStatus.NOT_FOUND));
            entity.setCustomer(customer);
        }

        // Validate unique preference key (excluding current preference)
        if (!entity.getPreferenceKey().equals(request.getPreferenceKey()) &&
            preferenceRepository.existsByCustomerIdAndPreferenceKey(request.getCustomerId(), request.getPreferenceKey())) {
            throw new GlobalException("PREFERENCE_KEY_EXISTS", HttpStatus.BAD_REQUEST);
        }

        preferenceMapper.updateEntity(request, entity);
        CustomerPreferenceEntity saved = preferenceRepository.save(entity);
        return preferenceMapper.toResponse(saved);
    }

    @Override
    public CustomerPreferenceResponse upsert(CustomerPreferenceRequest request) {
        // Validate customer exists
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.ERR002.name(), HttpStatus.NOT_FOUND));

        // Try to find existing preference
        Optional<CustomerPreferenceEntity> existing = preferenceRepository.findByCustomerIdAndPreferenceKey(
                request.getCustomerId(), request.getPreferenceKey());

        CustomerPreferenceEntity entity;
        if (existing.isPresent()) {
            // Update existing
            entity = existing.get();
            preferenceMapper.updateEntity(request, entity);
        } else {
            // Create new
            entity = preferenceMapper.toEntity(request);
            entity.setCustomer(customer);
        }

        CustomerPreferenceEntity saved = preferenceRepository.save(entity);
        return preferenceMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerPreferenceResponse getById(UUID id) {
        CustomerPreferenceEntity entity = preferenceRepository.findById(id)
                .orElseThrow(() -> new GlobalException("PREFERENCE_NOT_FOUND", HttpStatus.NOT_FOUND));
        return preferenceMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerPreferenceResponse getByCustomerIdAndPreferenceKey(UUID customerId, String preferenceKey) {
        CustomerPreferenceEntity entity = preferenceRepository.findByCustomerIdAndPreferenceKey(customerId, preferenceKey)
                .orElseThrow(() -> new GlobalException("PREFERENCE_NOT_FOUND", HttpStatus.NOT_FOUND));
        return preferenceMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerPreferenceResponse> getByCustomerId(UUID customerId) {
        List<CustomerPreferenceEntity> entities = preferenceRepository.findByCustomerId(customerId);
        return entities.stream()
                .map(preferenceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerPreferenceResponse> getByCustomerIdAndCategory(UUID customerId, String category) {
        List<CustomerPreferenceEntity> entities = preferenceRepository.findByCustomerIdAndCategory(customerId, category);
        return entities.stream()
                .map(preferenceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getPreferencesAsMap(UUID customerId) {
        List<CustomerPreferenceEntity> entities = preferenceRepository.findByCustomerId(customerId);
        Map<String, String> preferencesMap = new HashMap<>();
        entities.forEach(pref -> preferencesMap.put(pref.getPreferenceKey(), pref.getPreferenceValue()));
        return preferencesMap;
    }

    @Override
    public void delete(UUID id) {
        CustomerPreferenceEntity entity = preferenceRepository.findById(id)
                .orElseThrow(() -> new GlobalException("PREFERENCE_NOT_FOUND", HttpStatus.NOT_FOUND));
        preferenceRepository.delete(entity);
    }

    @Override
    public void deleteByCustomerIdAndPreferenceKey(UUID customerId, String preferenceKey) {
        CustomerPreferenceEntity entity = preferenceRepository.findByCustomerIdAndPreferenceKey(customerId, preferenceKey)
                .orElseThrow(() -> new GlobalException("PREFERENCE_NOT_FOUND", HttpStatus.NOT_FOUND));
        preferenceRepository.delete(entity);
    }

    @Override
    public void deleteAllByCustomerId(UUID customerId) {
        List<CustomerPreferenceEntity> entities = preferenceRepository.findByCustomerId(customerId);
        preferenceRepository.deleteAll(entities);
    }
}

