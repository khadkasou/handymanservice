package com.nepbrain.technology.customer.service.impl;

import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.PaginationRequest;
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
    public CustomerResponse create(CustomerRequest request) {
        // Validate unique customer code
        if (customerRepository.existsByCustomerCode(request.getCustomerCode())) {
            throw new GlobalException(ErrorCodeEnum.ERR001.name(), HttpStatus.BAD_REQUEST);
        }

        // Validate unique email
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new GlobalException(ErrorCodeEnum.ERR001.name(), HttpStatus.BAD_REQUEST);
        }

        CustomerEntity entity = customerMapper.toEntity(request);
        CustomerEntity saved = customerRepository.save(entity);
        return customerMapper.toResponse(saved);
    }

    @Override
    public CustomerResponse update(UUID id, CustomerRequest request) {
        CustomerEntity entity = customerRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.ERR002.name(), HttpStatus.NOT_FOUND));

        // Validate unique customer code (excluding current customer)
        if (!entity.getCustomerCode().equals(request.getCustomerCode()) && 
            customerRepository.existsByCustomerCode(request.getCustomerCode())) {
            throw new GlobalException(ErrorCodeEnum.ERR001.name(), HttpStatus.BAD_REQUEST);
        }

        // Validate unique email (excluding current customer)
        if (!entity.getEmail().equals(request.getEmail()) && 
            customerRepository.existsByEmail(request.getEmail())) {
            throw new GlobalException(ErrorCodeEnum.ERR001.name(), HttpStatus.BAD_REQUEST);
        }

        customerMapper.updateEntity(request, entity);
        CustomerEntity saved = customerRepository.save(entity);
        return customerMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getById(UUID id) {
        CustomerEntity entity = customerRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.ERR002.name(), HttpStatus.NOT_FOUND));
        return customerMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getByCustomerCode(String customerCode) {
        CustomerEntity entity = customerRepository.findByCustomerCode(customerCode)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.ERR002.name(), HttpStatus.NOT_FOUND));
        return customerMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getByEmail(String email) {
        CustomerEntity entity = customerRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.ERR002.name(), HttpStatus.NOT_FOUND));
        return customerMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse getAll(PaginationRequest paginationRequest) {
        Pageable pageable = PaginationHelper.getPageable(paginationRequest);
        Page<CustomerEntity> page = customerRepository.findAll(pageable);

        PaginationResponse response = new PaginationResponse();
        response.setTotalElements(page.getTotalElements());
        response.setResult(page.getContent().stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList()));
        return response;
    }

    @Override
    public void delete(UUID id) {
        CustomerEntity entity = customerRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.ERR002.name(), HttpStatus.NOT_FOUND));
        customerRepository.delete(entity);
    }
}

