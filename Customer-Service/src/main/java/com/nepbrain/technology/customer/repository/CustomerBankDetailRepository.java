package com.nepbrain.technology.customer.repository;

import com.nepbrain.technology.core.repsoitory.BaseRepository;
import com.nepbrain.technology.customer.entity.CustomerBankDetailEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerBankDetailRepository extends BaseRepository<CustomerBankDetailEntity, UUID> {
    List<CustomerBankDetailEntity> findByCustomerId(UUID customerId);
    Optional<CustomerBankDetailEntity> findByCustomerIdAndIsPrimaryTrue(UUID customerId);
}

