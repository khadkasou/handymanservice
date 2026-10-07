package com.nepbrain.technology.communication.repository;

import com.nepbrain.technology.communication.entity.CompanyEmailConfigEntity;
import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyEmailConfigRepository extends BaseRepository<CompanyEmailConfigEntity, UUID> {

    Optional<CompanyEmailConfigEntity> findByCompanyId(UUID companyId);
}
