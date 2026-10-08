package com.nepbrain.technology.customer.service.impl;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import com.nepbrain.technology.customer.entity.CustomerAddressEntity;
import com.nepbrain.technology.customer.entity.CustomerEntity;
import com.nepbrain.technology.customer.enums.AddressTypeEnum;
import com.nepbrain.technology.customer.enums.ErrorCodeEnum;
import com.nepbrain.technology.customer.mapper.CustomerAddressMapper;
import com.nepbrain.technology.customer.payload.request.CustomerAddressRequest;
import com.nepbrain.technology.customer.payload.response.CustomerAddressResponse;
import com.nepbrain.technology.customer.repository.CustomerAddressRepository;
import com.nepbrain.technology.customer.repository.CustomerRepository;
import com.nepbrain.technology.customer.service.CustomerAddressService;
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
public class CustomerAddressServiceImpl implements CustomerAddressService {

    private final CustomerAddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final CustomerAddressMapper addressMapper;

    @Override
    public GlobalResponse create(CustomerAddressRequest request) {
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUS002.name()));

        // If this is set as primary, unset other primary addresses
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            addressRepository.findByCustomerIdAndIsPrimaryTrue(request.getCustomerId())
                    .ifPresent(existing -> {
                        existing.setIsPrimary(false);
                        addressRepository.save(existing);
                    });
        }

        CustomerAddressEntity entity = addressMapper.toEntity(request);
        entity.setCustomer(customer);
        if (entity.getStatus() == null) {
            entity.setStatus(StatusEnum.ACTIVE);
        }
        CustomerAddressEntity saved = addressRepository.save(entity);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.address.create.success", addressMapper.toResponse(saved));
    }

    @Override
    public GlobalResponse update(UUID id, CustomerAddressRequest request) {
        CustomerAddressEntity entity = addressRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSA001.name()));

        if (!entity.getCustomer().getId().equals(request.getCustomerId())) {
            CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUS002.name()));
            entity.setCustomer(customer);
        }

        // If this is set as primary, unset other primary addresses
        if (Boolean.TRUE.equals(request.getIsPrimary()) && !Boolean.TRUE.equals(entity.getIsPrimary())) {
            addressRepository.findByCustomerIdAndIsPrimaryTrue(request.getCustomerId())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        existing.setIsPrimary(false);
                        addressRepository.save(existing);
                    });
        }

        addressMapper.updateEntity(request, entity);
        CustomerAddressEntity saved = addressRepository.save(entity);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.address.update.success", addressMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getById(UUID id) {
        CustomerAddressEntity entity = addressRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSA001.name()));
        return GlobalResponseBuilder.buildSuccessResponse(addressMapper.toResponse(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getByCustomerId(UUID customerId) {
        List<CustomerAddressResponse> addresses = addressRepository.findByCustomerId(customerId).stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
        return GlobalResponseBuilder.buildSuccessResponse(addresses);
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getByCustomerIdAndAddressType(UUID customerId, String addressType) {
        AddressTypeEnum type;
        try {
            type = AddressTypeEnum.valueOf(addressType.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new GlobalException(ErrorCodeEnum.CUSA004.name(), HttpStatus.BAD_REQUEST);
        }
        List<CustomerAddressResponse> addresses = addressRepository.findByCustomerIdAndAddressType(customerId, type).stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
        return GlobalResponseBuilder.buildSuccessResponse(addresses);
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse getPrimaryByCustomerId(UUID customerId) {
        CustomerAddressEntity entity = addressRepository.findByCustomerIdAndIsPrimaryTrue(customerId)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSA002.name()));
        return GlobalResponseBuilder.buildSuccessResponse(addressMapper.toResponse(entity));
    }

    @Override
    public GlobalResponse changeStatus(UUID id, StatusEnum status) {
        if (status != StatusEnum.ACTIVE && status != StatusEnum.INACTIVE) {
            throw new GlobalException(ErrorCodeEnum.CUSA003.name());
        }
        CustomerAddressEntity entity = addressRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.CUSA001.name(), HttpStatus.BAD_REQUEST));
        entity.setStatus(status);
        return GlobalResponseBuilder.buildSuccessResponse("message.customer.address.status.success", addressMapper.toResponse(addressRepository.save(entity)));
    }
}

