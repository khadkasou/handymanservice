package com.nepbrain.technology.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.core.enums.ApiStatusEnum;
import com.nepbrain.technology.notification.payload.response.NotificationResponse;
import com.nepbrain.technology.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
    }

    @Test
    void notificationList_withCustomerId() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(notificationService.notificationList(customerId)).thenReturn(List.of(
                NotificationResponse.builder()
                        .id(UUID.randomUUID())
                        .customerId(customerId)
                        .subject("Hello")
                        .isRead(false)
                        .build()));

        mockMvc.perform(post("/api/v2/notification/notification-list")
                        .param("customer_id", customerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ApiStatusEnum.SUCCESS.name()))
                .andExpect(jsonPath("$.code").value("NOTIFICATION_LIST"))
                .andExpect(jsonPath("$.data[0].subject").value("Hello"));

        verify(notificationService).notificationList(eq(customerId));
    }

    @Test
    void notificationList_withoutCustomerId() throws Exception {
        when(notificationService.notificationList(null)).thenReturn(List.of());

        mockMvc.perform(post("/api/v2/notification/notification-list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("NOTIFICATION_LIST"));

        verify(notificationService).notificationList(isNull());
    }
}
