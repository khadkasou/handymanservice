package com.nepbrain.technology.communication.repository;

import com.nepbrain.technology.communication.entity.SettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettingRepository extends JpaRepository<SettingEntity, UUID>, JpaSpecificationExecutor<SettingEntity> {

    Optional<SettingEntity> findBySettingCode(String settingCode);
}
