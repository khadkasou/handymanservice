package com.nepbrain.technology.customer.service.impl;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import com.nepbrain.technology.customer.entity.CustomerDetailEntity;
import com.nepbrain.technology.customer.entity.CustomerEntity;
import com.nepbrain.technology.customer.enums.ErrorCodeEnum;
import com.nepbrain.technology.customer.mapper.CustomerDetailMapper;
import com.nepbrain.technology.customer.payload.request.CustomerDetailRequest;
import com.nepbrain.technology.customer.payload.response.CustomerDetailResponse;
import com.nepbrain.technology.customer.repository.CustomerDetailRepository;
import com.nepbrain.technology.customer.repository.CustomerRepository;
import com.nepbrain.technology.customer.service.CustomerDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerDetailServiceImpl implements CustomerDetailService {

    private final CustomerDetailRepository detailRepository;
    private final CustomerRepository customerRepository;
    private final CustomerDetailMapper detailMapper;

    @Override
    public GlobalResponse create(CustomerDetailRequest request) {
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUS002.name()));

        if (detailRepository.existsByCustomerId(request.getCustomerId())) {
            throw new GlobalException(ErrorCodeEnum.CUSD002.name(), HttpStatus.CONFLICT);
        }

        CustomerDetailEntity entity = detailMapper.toEntity(request);
        entity.setCustomer(customer);
        if (entity.getStatus() == null) {
            entity.setStatus(StatusEnum.ACTIVE);
        }
        CustomerDetailEntity saved = detailRepository.save(entity);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.detail.create.success", detailMapper.toResponse(saved));
    }

    @Override
    public GlobalResponse update(UUID id, CustomerDetailRequest request) {
        CustomerDetailEntity entity = detailRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSD001.name()));

        if (!entity.getCustomer().getId().equals(request.getCustomerId())) {
            CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUS002.name()));
            entity.setCustomer(customer);
        }

        detailMapper.updateEntity(request, entity);
        CustomerDetailEntity saved = detailRepository.save(entity);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.detail.update.success", detailMapper.toResponse(saved));
    }

    @Override
    public GlobalResponse updateByCustomerId(UUID customerId, CustomerDetailRequest request) {
        CustomerDetailEntity entity = detailRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSD001.name()));

        detailMapper.updateEntity(request, entity);
        CustomerDetailEntity saved = detailRepository.save(entity);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.detail.update.success", detailMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getById(UUID id) {
        CustomerDetailEntity entity = detailRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSD001.name()));
        return GlobalResponseBuilder.buildSuccessResponse(detailMapper.toResponse(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getByCustomerId(UUID customerId) {
        CustomerDetailEntity entity = detailRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSD001.name()));
        return GlobalResponseBuilder.buildSuccessResponse(detailMapper.toResponse(entity));
    }

    @Override
    public GlobalResponse changeStatus(UUID id, StatusEnum status) {
        if (status != StatusEnum.ACTIVE && status != StatusEnum.INACTIVE) {
            throw new GlobalException(ErrorCodeEnum.CUSD003.name());
        }
        CustomerDetailEntity entity = detailRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSD001.name(), HttpStatus.BAD_REQUEST));
        entity.setStatus(status);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.detail.status.success", detailMapper.toResponse(detailRepository.save(entity)));
    }

    @Override
    public GlobalResponse changeStatusByCustomerId(UUID customerId, StatusEnum status) {
        if (status != StatusEnum.ACTIVE && status != StatusEnum.INACTIVE) {
            throw new GlobalException(ErrorCodeEnum.CUSD003.name());
        }
        CustomerDetailEntity entity = detailRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSD001.name(), HttpStatus.BAD_REQUEST));
        entity.setStatus(status);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.detail.status.success", detailMapper.toResponse(detailRepository.save(entity)));
    }
}

