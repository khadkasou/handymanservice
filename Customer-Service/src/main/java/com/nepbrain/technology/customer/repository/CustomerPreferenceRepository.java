package com.nepbrain.technology.customer.repository;

import com.nepbrain.technology.core.repsoitory.BaseRepository;
import com.nepbrain.technology.customer.entity.CustomerPreferenceEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerPreferenceRepository extends BaseRepository<CustomerPreferenceEntity, UUID> {
    List<CustomerPreferenceEntity> findByCustomerId(UUID customerId);
    Optional<CustomerPreferenceEntity> findByCustomerIdAndPreferenceKey(UUID customerId, String preferenceKey);
    List<CustomerPreferenceEntity> findByCustomerIdAndCategory(UUID customerId, String category);
    boolean existsByCustomerIdAndPreferenceKey(UUID customerId, String preferenceKey);
}

