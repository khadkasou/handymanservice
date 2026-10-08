package com.nepbrain.technology.payment.repository;

import com.nepbrain.technology.core.repsoitory.BaseRepository;
import com.nepbrain.technology.payment.entity.BankEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BankRepository extends BaseRepository<BankEntity, UUID> {

    List<BankEntity> findByUserId(UUID userId);
}
