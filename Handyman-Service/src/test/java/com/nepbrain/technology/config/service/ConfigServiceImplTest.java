package com.nepbrain.technology.config.service;

import com.nepbrain.technology.core.enums.ApiStatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import com.nepbrain.technology.config.service.impl.ConfigServiceImpl;
import com.nepbrain.technology.core.enums.StatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ConfigServiceImplTest {

    @Mock private AppConfigurationRepository appConfigurationRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ServiceRepository serviceRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ConfigServiceImpl configService;

    @Test
    void configurations_success() {
        AppConfigurationEntity cfg = AppConfigurationEntity.builder()
                .key("app_name")
                .value("Handyman")
                .type("string")
                .build();
        cfg.setId(UUID.randomUUID());
        when(appConfigurationRepository.findAll()).thenReturn(List.of(cfg));

        ConfigurationResponse response = (ConfigurationResponse) configService.configurations(true).getData();
        assertThat(response.getConfigurations()).containsEntry("app_name", "Handyman");
        verify(appConfigurationRepository).findAll();
    }

    @Test
    void configurations_nullValueBecomesEmptyString() {
        AppConfigurationEntity cfg = AppConfigurationEntity.builder().key("empty_key").value(null).build();
        cfg.setId(UUID.randomUUID());
        when(appConfigurationRepository.findAll()).thenReturn(List.of(cfg));

        ConfigurationResponse response = (ConfigurationResponse) configService.configurations(false).getData();
        assertThat(response.getConfigurations()).containsEntry("empty_key", "");
    }

    @Test
    void dashboardDetail_success() {
        CategoryEntity category = CategoryEntity.builder()
                .name("Plumbing")
                .image("cat.png")
                .isFeatured(true)
                .status(StatusEnum.ACTIVE)
                .build();
        category.setId(UUID.randomUUID());

        ServiceEntity featured = ServiceEntity.builder()
                .name("Pipe Fix")
                .price(new BigDecimal("100"))
                .image("svc.png")
                .isFeatured(true)
                .status(StatusEnum.ACTIVE)
                .build();
        featured.setId(UUID.randomUUID());
        ServiceEntity regular = ServiceEntity.builder()
                .name("Other")
                .price(new BigDecimal("50"))
                .isFeatured(false)
                .status(StatusEnum.ACTIVE)
                .build();
        regular.setId(UUID.randomUUID());

        UserEntity provider = UserEntity.builder()
                .email("p@test.com")
                .username("provider1")
                .passwordHash("hash")
                .userType(UserTypeEnum.PROVIDER)
                .displayName("Pro Handyman")
                .profileImage("p.png")
                .build();
        provider.setId(UUID.randomUUID());

        AppConfigurationEntity cfg = AppConfigurationEntity.builder().key("theme").value("light").build();
        cfg.setId(UUID.randomUUID());

        when(categoryRepository.findByParentIdIsNull()).thenReturn(List.of(category));
        when(serviceRepository.findAll()).thenReturn(List.of(featured, regular));
        when(userRepository.findByUserType(UserTypeEnum.PROVIDER)).thenReturn(List.of(provider));
        when(appConfigurationRepository.findAll()).thenReturn(List.of(cfg));

        DashboardResponse response = (DashboardResponse) configService.dashboardDetail().getData();
        assertThat(response.getCategory()).hasSize(1);
        assertThat(response.getService()).hasSize(2);
        assertThat(response.getProvider()).hasSize(1);
        assertThat(response.getFeaturedServices()).hasSize(1);
        assertThat(response.getFeaturedServices().getFirst().get("name")).isEqualTo("Pipe Fix");
        assertThat(response.getConfigurations()).containsEntry("theme", "light");
        verify(categoryRepository).findByParentIdIsNull();
        verify(userRepository).findByUserType(UserTypeEnum.PROVIDER);
    }

    @Test
    void firebaseDetail_success() {
        when(appConfigurationRepository.findAll()).thenReturn(List.of(config("firebase_api_key", "api"),
                config("firebase_app_id", "app"),
                config("firebase_messaging_sender_id", "sender"),
                config("firebase_project_id", "project"),
                config("firebase_storage_bucket", "bucket")));

        FirebaseDetailResponse response = (FirebaseDetailResponse) configService.firebaseDetail().getData();
        assertThat(response.getApiKey()).isEqualTo("api");
        assertThat(response.getAppId()).isEqualTo("app");
        assertThat(response.getMessagingSenderId()).isEqualTo("sender");
        assertThat(response.getProjectId()).isEqualTo("project");
        assertThat(response.getStorageBucket()).isEqualTo("bucket");
        verify(appConfigurationRepository).findAll();
    }

    @Test
    void firebaseDetail_missingKeys_defaultEmpty() {
        when(appConfigurationRepository.findAll()).thenReturn(List.of());

        FirebaseDetailResponse response = (FirebaseDetailResponse) configService.firebaseDetail().getData();
        assertThat(response.getApiKey()).isEmpty();
        assertThat(response.getAppId()).isEmpty();
        assertThat(response.getProjectId()).isEmpty();
    }

    private static AppConfigurationEntity config(String key, String value) {
        AppConfigurationEntity e = AppConfigurationEntity.builder().key(key).value(value).build();
        e.setId(UUID.randomUUID());
        return e;
    }
}
