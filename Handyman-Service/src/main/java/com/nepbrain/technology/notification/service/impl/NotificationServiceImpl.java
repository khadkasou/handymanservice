package com.nepbrain.technology.notification.service.impl;
import com.nepbrain.technology.notification.payload.response.NotificationResponse;
import com.nepbrain.technology.notification.repository.NotificationRepository;
import com.nepbrain.technology.notification.service.NotificationService;
import lombok.RequiredArgsConstructor; import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID; import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository; private final ModelMapper modelMapper;
    @Override public List<NotificationResponse> notificationList(UUID customerId) {
        return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(n -> modelMapper.map(n, NotificationResponse.class)).collect(Collectors.toList());
    }
}
