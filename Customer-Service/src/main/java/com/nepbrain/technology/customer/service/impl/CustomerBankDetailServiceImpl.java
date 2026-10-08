package com.nepbrain.technology.customer.service.impl;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
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
    public GlobalResponse create(CustomerBankDetailRequest request) {
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUS002.name()));

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
        if (entity.getStatus() == null) {
            entity.setStatus(StatusEnum.ACTIVE);
        }
        CustomerBankDetailEntity saved = bankDetailRepository.save(entity);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.bank.create.success", bankDetailMapper.toResponse(saved));
    }

    @Override
    public GlobalResponse update(UUID id, CustomerBankDetailRequest request) {
        CustomerBankDetailEntity entity = bankDetailRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSB001.name()));

        if (!entity.getCustomer().getId().equals(request.getCustomerId())) {
            CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUS002.name()));
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
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.bank.update.success", bankDetailMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getById(UUID id) {
        CustomerBankDetailEntity entity = bankDetailRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSB001.name()));
        return GlobalResponseBuilder.buildSuccessResponse(bankDetailMapper.toResponse(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getByCustomerId(UUID customerId) {
        List<CustomerBankDetailResponse> bankDetails = bankDetailRepository.findByCustomerId(customerId).stream()
                .map(bankDetailMapper::toResponse)
                .collect(Collectors.toList());
        return GlobalResponseBuilder.buildSuccessResponse(bankDetails);
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getPrimaryByCustomerId(UUID customerId) {
        CustomerBankDetailEntity entity = bankDetailRepository.findByCustomerIdAndIsPrimaryTrue(customerId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSB002.name()));
        return GlobalResponseBuilder.buildSuccessResponse(bankDetailMapper.toResponse(entity));
    }

    @Override
    public GlobalResponse changeStatus(UUID id, StatusEnum status) {
        if (status != StatusEnum.ACTIVE && status != StatusEnum.INACTIVE) {
            throw new GlobalException(ErrorCodeEnum.CUSB003.name());
        }
        CustomerBankDetailEntity entity = bankDetailRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSB001.name(), HttpStatus.BAD_REQUEST));
        entity.setStatus(status);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.bank.status.success", bankDetailMapper.toResponse(bankDetailRepository.save(entity)));
    }
}

