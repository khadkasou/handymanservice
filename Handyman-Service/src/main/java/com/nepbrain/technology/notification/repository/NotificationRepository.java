package com.nepbrain.technology.notification.repository;

import com.nepbrain.technology.core.repsoitory.BaseRepository;
import com.nepbrain.technology.notification.entity.NotificationEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends BaseRepository<NotificationEntity, UUID> {

    List<NotificationEntity> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
}
