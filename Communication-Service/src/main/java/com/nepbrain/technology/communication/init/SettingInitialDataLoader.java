package com.nepbrain.technology.communication.init;

import com.nepbrain.technology.communication.entity.SettingCategoryEntity;
import com.nepbrain.technology.communication.entity.SettingDetailEntity;
import com.nepbrain.technology.communication.entity.SettingEntity;
import com.nepbrain.technology.communication.enums.SettingCategoryEnum;
import com.nepbrain.technology.communication.enums.SettingDetailEnum;
import com.nepbrain.technology.communication.enums.SettingEnum;
import com.nepbrain.technology.communication.repository.SettingCategoryRepository;
import com.nepbrain.technology.communication.repository.SettingDetailRepository;
import com.nepbrain.technology.communication.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Runs on startup: inserts initial setting, setting_category, and setting_detail rows from enums.
 * Only inserts rows that do not already exist (by code/key), so safe to run on every startup.
 * Skips entirely when communication.initial-data.load-on-startup is false.
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class SettingInitialDataLoader implements CommandLineRunner {

    @Value("${communication.initial-data.load-on-startup:true}")
    private boolean loadOnStartup;

    private final SettingRepository settingRepository;
    private final SettingCategoryRepository settingCategoryRepository;
    private final SettingDetailRepository settingDetailRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (!loadOnStartup) {
            log.debug("Initial data loader disabled (communication.initial-data.load-on-startup=false).");
            return;
        }

        log.info("Inserting setting, setting_category and setting_detail on startup (missing rows only)...");

        int settingsInserted = 0;
        for (SettingEnum e : SettingEnum.values()) {
            if (settingRepository.findBySettingCode(e.getCode()).isEmpty()) {
                SettingEntity entity = SettingEntity.builder()
                        .settingCode(e.getCode())
                        .name(e.getDescription())
                        .description(null)
                        .build();
                settingRepository.save(entity);
                settingsInserted++;
            }
        }

        int categoriesInserted = 0;
        for (SettingCategoryEnum e : SettingCategoryEnum.values()) {
            SettingEntity setting = settingRepository.findBySettingCode(e.getSettingCode()).orElse(null);
            if (setting == null) continue;
            if (settingCategoryRepository.findBySettingAndCategoryCode(setting, e.getCode()).isEmpty()) {
                SettingCategoryEntity category = SettingCategoryEntity.builder()
                        .categoryCode(e.getCode())
                        .name(e.getDescription())
                        .description(null)
                        .setting(setting)
                        .build();
                settingCategoryRepository.save(category);
                categoriesInserted++;
            }
        }

        int detailsInserted = 0;
        for (SettingDetailEnum e : SettingDetailEnum.values()) {
            SettingCategoryEntity category = settingCategoryRepository.findByCategoryCode(e.getCategoryCode()).orElse(null);
            if (category == null) continue;
            if (settingDetailRepository.findBySettingCategoryAndKey(category, e.getCode()).isEmpty()) {
                SettingDetailEntity detail = SettingDetailEntity.builder()
                        .key(e.getCode())
                        .displayName(e.getDescription())
                        .value(null)
                        .settingCategory(category)
                        .build();
                settingDetailRepository.save(detail);
                detailsInserted++;
            }
        }

        log.info("Startup insert completed: setting={}, setting_category={}, setting_detail={}.",
                settingsInserted, categoriesInserted, detailsInserted);
    }
}
