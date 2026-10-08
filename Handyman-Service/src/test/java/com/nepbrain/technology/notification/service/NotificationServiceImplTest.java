package com.nepbrain.technology.notification.service;

import com.nepbrain.technology.core.enums.ApiStatusEnum;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.nepbrain.technology.notification.entity.NotificationEntity;
import com.nepbrain.technology.notification.payload.response.NotificationResponse;
import com.nepbrain.technology.notification.repository.NotificationRepository;
import com.nepbrain.technology.notification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock private NotificationRepository notificationRepository;

    @Spy private ModelMapper modelMapper = configuredMapper();

    @InjectMocks private NotificationServiceImpl notificationService;

    private static ModelMapper configuredMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);
        return mapper;
    }

    @Test
    void notificationList_success() {
        UUID customerId = UUID.randomUUID();
        NotificationEntity entity = NotificationEntity.builder()
                .customerId(customerId)
                .type("booking")
                .subject("Update")
                .message("Your booking was accepted")
                .isRead(false)
                .dataId(UUID.randomUUID())
                .build();
        entity.setId(UUID.randomUUID());
        when(notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)).thenReturn(List.of(entity));

        List<NotificationResponse> result = (List<NotificationResponse>) notificationService.notificationList(customerId).getData();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCustomerId()).isEqualTo(customerId);
        assertThat(result.getFirst().getSubject()).isEqualTo("Update");
        assertThat(result.getFirst().getIsRead()).isFalse();
        verify(notificationRepository).findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Test
    void notificationList_empty() {
        UUID customerId = UUID.randomUUID();
        when(notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)).thenReturn(List.of());

        List<NotificationResponse> result = (List<NotificationResponse>) notificationService.notificationList(customerId).getData();
        assertThat(result).isEmpty();
        verify(notificationRepository).findByCustomerIdOrderByCreatedAtDesc(customerId);
    }
}
