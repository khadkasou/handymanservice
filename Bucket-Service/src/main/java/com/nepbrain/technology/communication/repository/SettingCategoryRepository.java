package com.nepbrain.technology.communication.repository;

import com.nepbrain.technology.communication.entity.SettingCategoryEntity;
import com.nepbrain.technology.communication.entity.SettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettingCategoryRepository extends JpaRepository<SettingCategoryEntity, UUID>, JpaSpecificationExecutor<SettingCategoryEntity> {

    Optional<SettingCategoryEntity> findBySettingAndCategoryCode(SettingEntity setting, String categoryCode);

    Optional<SettingCategoryEntity> findByCategoryCode(String categoryCode);

    List<SettingCategoryEntity> findAllBySetting(SettingEntity setting);
}
