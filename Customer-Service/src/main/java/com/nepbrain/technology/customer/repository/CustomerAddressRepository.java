package com.nepbrain.technology.customer.repository;

import com.nepbrain.technology.core.repsoitory.BaseRepository;
import com.nepbrain.technology.customer.entity.CustomerAddressEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerAddressRepository extends BaseRepository<CustomerAddressEntity, UUID> {
    List<CustomerAddressEntity> findByCustomerId(UUID customerId);
    Optional<CustomerAddressEntity> findByCustomerIdAndIsPrimaryTrue(UUID customerId);
    List<CustomerAddressEntity> findByCustomerIdAndAddressType(UUID customerId, com.nepbrain.technology.customer.enums.AddressTypeEnum addressType);
}

