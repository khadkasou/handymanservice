package com.nepbrain.technology.communication.repository;

import com.nepbrain.technology.communication.entity.CompanySmsConfigEntity;
import com.nepbrain.technology.communication.enums.SmsProviderEnum;
import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanySmsConfigRepository extends BaseRepository<CompanySmsConfigEntity, UUID> {

    List<CompanySmsConfigEntity> findByCompanyId(UUID companyId);

    Optional<CompanySmsConfigEntity> findByCompanyIdAndProvider(UUID companyId, SmsProviderEnum provider);
}
