package com.nepbrain.technology.customer.service.impl;

import com.nepbrain.technology.core.exception.GlobalException;
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
    public CustomerDetailResponse create(CustomerDetailRequest request) {
        // Validate customer exists
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.ERR002.name(), HttpStatus.NOT_FOUND));

        // Check if detail already exists
        if (detailRepository.existsByCustomerId(request.getCustomerId())) {
            throw new GlobalException("CUSTOMER_DETAIL_EXISTS", HttpStatus.BAD_REQUEST);
        }

        CustomerDetailEntity entity = detailMapper.toEntity(request);
        entity.setCustomer(customer);
        CustomerDetailEntity saved = detailRepository.save(entity);
        return detailMapper.toResponse(saved);
    }

    @Override
    public CustomerDetailResponse update(UUID id, CustomerDetailRequest request) {
        CustomerDetailEntity entity = detailRepository.findById(id)
                .orElseThrow(() -> new GlobalException("CUSTOMER_DETAIL_NOT_FOUND", HttpStatus.NOT_FOUND));

        // Validate customer exists if changed
        if (!entity.getCustomer().getId().equals(request.getCustomerId())) {
            CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new GlobalException(ErrorCodeEnum.ERR002.name(), HttpStatus.NOT_FOUND));
            entity.setCustomer(customer);
        }

        detailMapper.updateEntity(request, entity);
        CustomerDetailEntity saved = detailRepository.save(entity);
        return detailMapper.toResponse(saved);
    }

    @Override
    public CustomerDetailResponse updateByCustomerId(UUID customerId, CustomerDetailRequest request) {
        CustomerDetailEntity entity = detailRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new GlobalException("CUSTOMER_DETAIL_NOT_FOUND", HttpStatus.NOT_FOUND));

        detailMapper.updateEntity(request, entity);
        CustomerDetailEntity saved = detailRepository.save(entity);
        return detailMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDetailResponse getById(UUID id) {
        CustomerDetailEntity entity = detailRepository.findById(id)
                .orElseThrow(() -> new GlobalException("CUSTOMER_DETAIL_NOT_FOUND", HttpStatus.NOT_FOUND));
        return detailMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDetailResponse getByCustomerId(UUID customerId) {
        CustomerDetailEntity entity = detailRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new GlobalException("CUSTOMER_DETAIL_NOT_FOUND", HttpStatus.NOT_FOUND));
        return detailMapper.toResponse(entity);
    }

    @Override
    public void delete(UUID id) {
        CustomerDetailEntity entity = detailRepository.findById(id)
                .orElseThrow(() -> new GlobalException("CUSTOMER_DETAIL_NOT_FOUND", HttpStatus.NOT_FOUND));
        detailRepository.delete(entity);
    }

    @Override
    public void deleteByCustomerId(UUID customerId) {
        CustomerDetailEntity entity = detailRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new GlobalException("CUSTOMER_DETAIL_NOT_FOUND", HttpStatus.NOT_FOUND));
        detailRepository.delete(entity);
    }
}

