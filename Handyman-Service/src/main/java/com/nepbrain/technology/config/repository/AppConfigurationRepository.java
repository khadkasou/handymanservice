package com.nepbrain.technology.config.repository;

import com.nepbrain.technology.config.entity.AppConfigurationEntity;
import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppConfigurationRepository extends BaseRepository<AppConfigurationEntity, UUID> {

    Optional<AppConfigurationEntity> findByKey(String key);
}
