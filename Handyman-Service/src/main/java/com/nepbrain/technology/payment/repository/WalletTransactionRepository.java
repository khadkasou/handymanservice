package com.nepbrain.technology.payment.repository;

import com.nepbrain.technology.core.repsoitory.BaseRepository;
import com.nepbrain.technology.payment.entity.WalletTransactionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletTransactionRepository extends BaseRepository<WalletTransactionEntity, UUID> {

    List<WalletTransactionEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
