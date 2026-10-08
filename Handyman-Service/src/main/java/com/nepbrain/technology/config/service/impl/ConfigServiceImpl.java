package com.nepbrain.technology.config.service.impl;

import com.nepbrain.technology.auth.entity.UserEntity;
import com.nepbrain.technology.auth.enums.UserTypeEnum;
import com.nepbrain.technology.auth.repository.UserRepository;
import com.nepbrain.technology.catalog.entity.CategoryEntity;
import com.nepbrain.technology.catalog.entity.ServiceEntity;
import com.nepbrain.technology.catalog.repository.CategoryRepository;
import com.nepbrain.technology.catalog.repository.ServiceRepository;
import com.nepbrain.technology.config.entity.AppConfigurationEntity;
import com.nepbrain.technology.config.payload.response.ConfigurationResponse;
import com.nepbrain.technology.config.payload.response.DashboardResponse;
import com.nepbrain.technology.config.payload.response.FirebaseDetailResponse;
import com.nepbrain.technology.config.repository.AppConfigurationRepository;
import com.nepbrain.technology.config.service.ConfigService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private static final int DASHBOARD_LIMIT = 20;

    private final AppConfigurationRepository appConfigurationRepository;
    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    @Override
    public GlobalResponse configurations(boolean authenticated) {
        return GlobalResponseBuilder.buildSuccessResponse(ConfigurationResponse.builder()
                .configurations(loadConfig())
                .build());
    }

    @Override
    public GlobalResponse dashboardDetail() {
        List<Map<String, Object>> categories = new ArrayList<>();
        for (CategoryEntity category : categoryRepository.findByParentIdIsNull()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", category.getId());
            row.put("name", category.getName());
            row.put("image", category.getImage());
            row.put("isFeatured", category.getIsFeatured());
            categories.add(row);
        }

        List<Map<String, Object>> services = new ArrayList<>();
        int serviceCount = 0;
        for (ServiceEntity service : serviceRepository.findAll()) {
            if (serviceCount >= DASHBOARD_LIMIT) {
                break;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", service.getId());
            row.put("name", service.getName());
            row.put("price", service.getPrice());
            row.put("image", service.getImage());
            row.put("isFeatured", service.getIsFeatured());
            services.add(row);
            serviceCount++;
        }

        List<Map<String, Object>> providers = new ArrayList<>();
        int providerCount = 0;
        for (UserEntity provider : userRepository.findByUserType(UserTypeEnum.PROVIDER)) {
            if (providerCount >= DASHBOARD_LIMIT) {
                break;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", provider.getId());
            row.put("displayName", provider.getDisplayName());
            row.put("profileImage", provider.getProfileImage());
            providers.add(row);
            providerCount++;
        }

        List<Map<String, Object>> featured = new ArrayList<>();
        for (Map<String, Object> service : services) {
            if (Boolean.TRUE.equals(service.get("isFeatured"))) {
                featured.add(service);
            }
        }

        Map<String, Object> configs = new HashMap<>();
        configs.putAll(loadConfig());

        return GlobalResponseBuilder.buildSuccessResponse(DashboardResponse.builder()
                .category(categories)
                .service(services)
                .provider(providers)
                .featuredServices(featured)
                .configurations(configs)
                .build());
    }

    @Override
    public GlobalResponse firebaseDetail() {
        Map<String, String> config = loadConfig();
        return GlobalResponseBuilder.buildSuccessResponse(FirebaseDetailResponse.builder()
                .apiKey(config.getOrDefault("firebase_api_key", ""))
                .appId(config.getOrDefault("firebase_app_id", ""))
                .messagingSenderId(config.getOrDefault("firebase_messaging_sender_id", ""))
                .projectId(config.getOrDefault("firebase_project_id", ""))
                .storageBucket(config.getOrDefault("firebase_storage_bucket", ""))
                .build());
    }

    private Map<String, String> loadConfig() {
        Map<String, String> values = new HashMap<>();
        for (AppConfigurationEntity config : appConfigurationRepository.findAll()) {
            if (values.containsKey(config.getKey())) {
                continue;
            }
            String value = config.getValue() == null ? "" : config.getValue();
            values.put(config.getKey(), value);
        }
        return values;
    }
}
