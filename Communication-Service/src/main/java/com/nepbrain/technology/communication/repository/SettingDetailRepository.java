package com.nepbrain.technology.communication.repository;

import com.nepbrain.technology.communication.entity.SettingCategoryEntity;
import com.nepbrain.technology.communication.entity.SettingDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettingDetailRepository extends JpaRepository<SettingDetailEntity, UUID>, JpaSpecificationExecutor<SettingDetailEntity> {

    Optional<SettingDetailEntity> findBySettingCategoryAndKey(SettingCategoryEntity settingCategory, String key);

    Optional<SettingDetailEntity> findFirstByKeyIgnoreCase(String key);

    List<SettingDetailEntity> findAllBySettingCategory(SettingCategoryEntity settingCategory);
}
