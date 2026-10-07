package com.nepbrain.technology.config.controller;

import com.nepbrain.technology.config.payload.response.ConfigurationResponse;
import com.nepbrain.technology.config.payload.response.DashboardResponse;
import com.nepbrain.technology.config.payload.response.FirebaseDetailResponse;
import com.nepbrain.technology.config.service.ConfigService;
import com.nepbrain.technology.core.enums.ApiStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ConfigControllerTest {

    @Mock
    private ConfigService configService;

    @InjectMocks
    private ConfigController configController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(configController).build();
    }

    @Test
    void configurations_defaultUnauthenticated() throws Exception {
        when(configService.configurations(false)).thenReturn(ConfigurationResponse.builder()
                .configurations(Map.of("site_name", "Handyman"))
                .build());

        mockMvc.perform(get("/api/v2/config/configurations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()))
                .andExpect(jsonPath("$.code").value("CONFIGURATIONS"))
                .andExpect(jsonPath("$.data.configurations.site_name").value("Handyman"));

        verify(configService).configurations(false);
    }

    @Test
    void configurations_authenticated() throws Exception {
        when(configService.configurations(true)).thenReturn(ConfigurationResponse.builder()
                .configurations(Map.of("auth", "1"))
                .build());

        mockMvc.perform(get("/api/v2/config/configurations").param("is_authenticated", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("CONFIGURATIONS"))
                .andExpect(jsonPath("$.data.configurations.auth").value("1"));

        verify(configService).configurations(true);
    }

    @Test
    void dashboardDetail_returnsOk() throws Exception {
        when(configService.dashboardDetail()).thenReturn(DashboardResponse.builder()
                .category(List.of(Map.of("name", "Plumbing")))
                .service(List.of())
                .provider(List.of())
                .build());

        mockMvc.perform(get("/api/v2/config/dashboard-detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DASHBOARD_DETAIL"))
                .andExpect(jsonPath("$.data.category[0].name").value("Plumbing"));

        verify(configService).dashboardDetail();
    }

    @Test
    void dashboardDetail_trailingSlash_returnsOk() throws Exception {
        when(configService.dashboardDetail()).thenReturn(DashboardResponse.builder().build());

        mockMvc.perform(get("/api/v2/config/dashboard-detail/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DASHBOARD_DETAIL"));

        verify(configService).dashboardDetail();
    }

    @Test
    void firebaseDetail_returnsOk() throws Exception {
        when(configService.firebaseDetail()).thenReturn(FirebaseDetailResponse.builder()
                .apiKey("key")
                .projectId("handyman")
                .appId("app")
                .build());

        mockMvc.perform(get("/api/v2/config/firebase-detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("FIREBASE_DETAIL"))
                .andExpect(jsonPath("$.data.projectId").value("handyman"))
                .andExpect(jsonPath("$.data.apiKey").value("key"));

        verify(configService).firebaseDetail();
    }
}
