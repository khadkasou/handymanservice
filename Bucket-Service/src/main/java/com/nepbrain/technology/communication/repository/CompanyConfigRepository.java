package com.nepbrain.technology.communication.repository;

import com.nepbrain.technology.communication.entity.CompanyConfigEntity;
import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyConfigRepository extends BaseRepository<CompanyConfigEntity, UUID> {

    Optional<CompanyConfigEntity> findByCompanyId(UUID companyId);
}
