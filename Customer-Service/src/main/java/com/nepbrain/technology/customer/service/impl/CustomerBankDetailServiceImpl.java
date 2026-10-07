package com.nepbrain.technology.customer.service.impl;

import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.customer.entity.CustomerBankDetailEntity;
import com.nepbrain.technology.customer.entity.CustomerEntity;
import com.nepbrain.technology.customer.enums.ErrorCodeEnum;
import com.nepbrain.technology.customer.mapper.CustomerBankDetailMapper;
import com.nepbrain.technology.customer.payload.request.CustomerBankDetailRequest;
import com.nepbrain.technology.customer.payload.response.CustomerBankDetailResponse;
import com.nepbrain.technology.customer.repository.CustomerBankDetailRepository;
import com.nepbrain.technology.customer.repository.CustomerRepository;
import com.nepbrain.technology.customer.service.CustomerBankDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerBankDetailServiceImpl implements CustomerBankDetailService {

    private final CustomerBankDetailRepository bankDetailRepository;
    private final CustomerRepository customerRepository;
    private final CustomerBankDetailMapper bankDetailMapper;

    @Override
    public CustomerBankDetailResponse create(CustomerBankDetailRequest request) {
        // Validate customer exists
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.ERR002.name(), HttpStatus.NOT_FOUND));

        // If this is set as primary, unset other primary bank details
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            bankDetailRepository.findByCustomerIdAndIsPrimaryTrue(request.getCustomerId())
                    .ifPresent(existing -> {
                        existing.setIsPrimary(false);
                        bankDetailRepository.save(existing);
                    });
        }

        CustomerBankDetailEntity entity = bankDetailMapper.toEntity(request);
        entity.setCustomer(customer);
        CustomerBankDetailEntity saved = bankDetailRepository.save(entity);
        return bankDetailMapper.toResponse(saved);
    }

    @Override
    public CustomerBankDetailResponse update(UUID id, CustomerBankDetailRequest request) {
        CustomerBankDetailEntity entity = bankDetailRepository.findById(id)
                .orElseThrow(() -> new GlobalException("CUSTOMER_BANK_DETAIL_NOT_FOUND", HttpStatus.NOT_FOUND));

        // Validate customer exists if changed
        if (!entity.getCustomer().getId().equals(request.getCustomerId())) {
            CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new GlobalException(ErrorCodeEnum.ERR002.name(), HttpStatus.NOT_FOUND));
            entity.setCustomer(customer);
        }

        // If this is set as primary, unset other primary bank details
        if (Boolean.TRUE.equals(request.getIsPrimary()) && !Boolean.TRUE.equals(entity.getIsPrimary())) {
            bankDetailRepository.findByCustomerIdAndIsPrimaryTrue(request.getCustomerId())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        existing.setIsPrimary(false);
                        bankDetailRepository.save(existing);
                    });
        }

        bankDetailMapper.updateEntity(request, entity);
        CustomerBankDetailEntity saved = bankDetailRepository.save(entity);
        return bankDetailMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerBankDetailResponse getById(UUID id) {
        CustomerBankDetailEntity entity = bankDetailRepository.findById(id)
                .orElseThrow(() -> new GlobalException("CUSTOMER_BANK_DETAIL_NOT_FOUND", HttpStatus.NOT_FOUND));
        return bankDetailMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerBankDetailResponse> getByCustomerId(UUID customerId) {
        List<CustomerBankDetailEntity> entities = bankDetailRepository.findByCustomerId(customerId);
        return entities.stream()
                .map(bankDetailMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerBankDetailResponse getPrimaryByCustomerId(UUID customerId) {
        CustomerBankDetailEntity entity = bankDetailRepository.findByCustomerIdAndIsPrimaryTrue(customerId)
                .orElseThrow(() -> new GlobalException("PRIMARY_BANK_DETAIL_NOT_FOUND", HttpStatus.NOT_FOUND));
        return bankDetailMapper.toResponse(entity);
    }

    @Override
    public void delete(UUID id) {
        CustomerBankDetailEntity entity = bankDetailRepository.findById(id)
                .orElseThrow(() -> new GlobalException("CUSTOMER_BANK_DETAIL_NOT_FOUND", HttpStatus.NOT_FOUND));
        bankDetailRepository.delete(entity);
    }
}

