package com.nepbrain.technology.customer.service.impl;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.PaginationRequest;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import com.nepbrain.technology.core.payload.response.PaginationResponse;
import com.nepbrain.technology.core.util.PaginationHelper;
import com.nepbrain.technology.customer.entity.CustomerEntity;
import com.nepbrain.technology.customer.enums.ErrorCodeEnum;
import com.nepbrain.technology.customer.mapper.CustomerMapper;
import com.nepbrain.technology.customer.payload.request.CustomerRequest;
import com.nepbrain.technology.customer.payload.response.CustomerResponse;
import com.nepbrain.technology.customer.repository.CustomerRepository;
import com.nepbrain.technology.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public GlobalResponse create(CustomerRequest request) {
        if (customerRepository.existsByCustomerCode(request.getCustomerCode())) {
            throw new GlobalException(ErrorCodeEnum.CUS001.name(), HttpStatus.CONFLICT);
        }
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new GlobalException(ErrorCodeEnum.CUS003.name(), HttpStatus.CONFLICT);
        }

        CustomerEntity entity = customerMapper.toEntity(request);
        if (entity.getStatus() == null) {
            entity.setStatus(StatusEnum.ACTIVE);
        }
        CustomerEntity saved = customerRepository.save(entity);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.create.success", customerMapper.toResponse(saved));
    }

    @Override
    public GlobalResponse update(UUID id, CustomerRequest request) {
        CustomerEntity entity = customerRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUS002.name()));

        if (!entity.getCustomerCode().equals(request.getCustomerCode()) &&
            customerRepository.existsByCustomerCode(request.getCustomerCode())) {
            throw new GlobalException(ErrorCodeEnum.CUS001.name(), HttpStatus.CONFLICT);
        }
        if (!entity.getEmail().equals(request.getEmail()) &&
            customerRepository.existsByEmail(request.getEmail())) {
            throw new GlobalException(ErrorCodeEnum.CUS003.name(), HttpStatus.CONFLICT);
        }

        customerMapper.updateEntity(request, entity);
        CustomerEntity saved = customerRepository.save(entity);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.update.success", customerMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getById(UUID id) {
        CustomerEntity entity = customerRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUS002.name()));
        return GlobalResponseBuilder.buildSuccessResponse(customerMapper.toResponse(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getByCustomerCode(String customerCode) {
        CustomerEntity entity = customerRepository.findByCustomerCode(customerCode)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUS002.name()));
        return GlobalResponseBuilder.buildSuccessResponse(customerMapper.toResponse(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getByEmail(String email) {
        CustomerEntity entity = customerRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUS002.name()));
        return GlobalResponseBuilder.buildSuccessResponse(customerMapper.toResponse(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getAll(PaginationRequest paginationRequest) {
        Pageable pageable = PaginationHelper.getPageable(paginationRequest);
        Page<CustomerEntity> page = customerRepository.findAll(pageable);

        PaginationResponse response = new PaginationResponse();
        response.setTotalElements(page.getTotalElements());
        response.setResult(page.getContent().stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList()));
        return GlobalResponseBuilder.buildSuccessResponse(response);
    }

    @Override
    public GlobalResponse changeStatus(UUID id, StatusEnum status) {
        if (status != StatusEnum.ACTIVE && status != StatusEnum.INACTIVE) {
            throw new GlobalException(ErrorCodeEnum.CUS004.name());
        }
        CustomerEntity entity = customerRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUS002.name(), HttpStatus.BAD_REQUEST));
        entity.setStatus(status);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.status.success", customerMapper.toResponse(customerRepository.save(entity)));
    }
}

