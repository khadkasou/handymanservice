package com.nepbrain.technology.customer.repository;

import com.nepbrain.technology.core.repsoitory.BaseRepository;
import com.nepbrain.technology.customer.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends BaseRepository<CustomerEntity, UUID> {
    Optional<CustomerEntity> findByCustomerCode(String customerCode);
    Optional<CustomerEntity> findByEmail(String email);
    boolean existsByCustomerCode(String customerCode);
    boolean existsByEmail(String email);
}

