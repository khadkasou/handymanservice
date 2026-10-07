package com.nepbrain.technology.customer.service.impl;

import com.nepbrain.technology.core.exception.GlobalException;
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
    public CustomerAddressResponse create(CustomerAddressRequest request) {
        // Validate customer exists
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new GlobalException(ErrorCodeEnum.ERR002.name(), HttpStatus.NOT_FOUND));

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
        CustomerAddressEntity saved = addressRepository.save(entity);
        return addressMapper.toResponse(saved);
    }

    @Override
    public CustomerAddressResponse update(UUID id, CustomerAddressRequest request) {
        CustomerAddressEntity entity = addressRepository.findById(id)
                .orElseThrow(() -> new GlobalException("CUSTOMER_ADDRESS_NOT_FOUND", HttpStatus.NOT_FOUND));

        // Validate customer exists if changed
        if (!entity.getCustomer().getId().equals(request.getCustomerId())) {
            CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new GlobalException(ErrorCodeEnum.ERR002.name(), HttpStatus.NOT_FOUND));
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
        return addressMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerAddressResponse getById(UUID id) {
        CustomerAddressEntity entity = addressRepository.findById(id)
                .orElseThrow(() -> new GlobalException("CUSTOMER_ADDRESS_NOT_FOUND", HttpStatus.NOT_FOUND));
        return addressMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerAddressResponse> getByCustomerId(UUID customerId) {
        List<CustomerAddressEntity> entities = addressRepository.findByCustomerId(customerId);
        return entities.stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerAddressResponse> getByCustomerIdAndAddressType(UUID customerId, AddressTypeEnum addressType) {
        List<CustomerAddressEntity> entities = addressRepository.findByCustomerIdAndAddressType(customerId, addressType);
        return entities.stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerAddressResponse getPrimaryByCustomerId(UUID customerId) {
        CustomerAddressEntity entity = addressRepository.findByCustomerIdAndIsPrimaryTrue(customerId)
                .orElseThrow(() -> new GlobalException("PRIMARY_ADDRESS_NOT_FOUND", HttpStatus.NOT_FOUND));
        return addressMapper.toResponse(entity);
    }

    @Override
    public void delete(UUID id) {
        CustomerAddressEntity entity = addressRepository.findById(id)
                .orElseThrow(() -> new GlobalException("CUSTOMER_ADDRESS_NOT_FOUND", HttpStatus.NOT_FOUND));
        addressRepository.delete(entity);
    }
}

