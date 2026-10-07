package com.nepbrain.technology.customer.repository;

import com.nepbrain.technology.core.repsoitory.BaseRepository;
import com.nepbrain.technology.customer.entity.CustomerDetailEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerDetailRepository extends BaseRepository<CustomerDetailEntity, UUID> {
    Optional<CustomerDetailEntity> findByCustomerId(UUID customerId);
    boolean existsByCustomerId(UUID customerId);
}

