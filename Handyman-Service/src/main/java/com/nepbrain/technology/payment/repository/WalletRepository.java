package com.nepbrain.technology.payment.repository;

import com.nepbrain.technology.core.repsoitory.BaseRepository;
import com.nepbrain.technology.payment.entity.WalletEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends BaseRepository<WalletEntity, UUID> {

    Optional<WalletEntity> findByUserId(UUID userId);
}
