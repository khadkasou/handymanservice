package com.nepbrain.technology.config.service.impl;
import com.nepbrain.technology.auth.enums.UserTypeEnum;
import com.nepbrain.technology.auth.repository.UserRepository;
import com.nepbrain.technology.catalog.repository.CategoryRepository;
import com.nepbrain.technology.catalog.repository.ServiceRepository;
import com.nepbrain.technology.config.entity.AppConfigurationEntity;
import com.nepbrain.technology.config.payload.response.*;
import com.nepbrain.technology.config.repository.AppConfigurationRepository;
import com.nepbrain.technology.config.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.*; import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class ConfigServiceImpl implements ConfigService {
    private final AppConfigurationRepository appConfigurationRepository;
    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    @Override public ConfigurationResponse configurations(boolean authenticated) {
        Map<String, String> map = appConfigurationRepository.findAll().stream()
                .collect(Collectors.toMap(AppConfigurationEntity::getKey, e -> e.getValue() != null ? e.getValue() : "", (a, b) -> a));
        return ConfigurationResponse.builder().configurations(map).build();
    }
    @Override public DashboardResponse dashboardDetail() {
        List<Map<String, Object>> categories = categoryRepository.findByParentIdIsNull().stream().map(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId()); m.put("name", c.getName()); m.put("image", c.getImage());
            m.put("isFeatured", c.getIsFeatured()); return m;
        }).collect(Collectors.toList());
        List<Map<String, Object>> services = serviceRepository.findAll().stream().limit(20).map(s -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", s.getId()); m.put("name", s.getName()); m.put("price", s.getPrice());
            m.put("image", s.getImage()); m.put("isFeatured", s.getIsFeatured()); return m;
        }).collect(Collectors.toList());
        List<Map<String, Object>> providers = userRepository.findByUserType(UserTypeEnum.PROVIDER).stream().limit(20).map(u -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId()); m.put("displayName", u.getDisplayName());
            m.put("profileImage", u.getProfileImage()); return m;
        }).collect(Collectors.toList());
        Map<String, Object> configs = appConfigurationRepository.findAll().stream()
                .collect(Collectors.toMap(AppConfigurationEntity::getKey, e -> e.getValue() != null ? e.getValue() : "", (a, b) -> a));
        return DashboardResponse.builder().category(categories).service(services).provider(providers)
                .featuredServices(services.stream().filter(s -> Boolean.TRUE.equals(s.get("isFeatured"))).collect(Collectors.toList()))
                .configurations(configs).build();
    }
    @Override public FirebaseDetailResponse firebaseDetail() {
        Map<String, String> map = appConfigurationRepository.findAll().stream()
                .collect(Collectors.toMap(AppConfigurationEntity::getKey, e -> e.getValue() != null ? e.getValue() : "", (a, b) -> a));
        return FirebaseDetailResponse.builder()
                .apiKey(map.getOrDefault("firebase_api_key", ""))
                .appId(map.getOrDefault("firebase_app_id", ""))
                .messagingSenderId(map.getOrDefault("firebase_messaging_sender_id", ""))
                .projectId(map.getOrDefault("firebase_project_id", ""))
                .storageBucket(map.getOrDefault("firebase_storage_bucket", ""))
                .build();
    }
}
