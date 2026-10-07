package com.nepbrain.technology.communication.repository;

import com.nepbrain.technology.communication.entity.UserNotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotificationEntity, UUID> {

    @Query("""
            SELECT n FROM UserNotificationEntity n
            WHERE n.userId = :userId
              AND n.archived = false
              AND (:read IS NULL OR n.read = :read)
              AND (:excludeCompanyNotice = false
                   OR n.category IS NULL
                   OR n.category <> 'COMPANY_NOTICE')
              AND (:searchPattern IS NULL
                   OR LOWER(n.title) LIKE :searchPattern
                   OR LOWER(n.body) LIKE :searchPattern
                   OR LOWER(COALESCE(n.topic, '')) LIKE :searchPattern
                   OR LOWER(COALESCE(n.notificationType, '')) LIKE :searchPattern
                   OR LOWER(COALESCE(n.category, '')) LIKE :searchPattern
                   OR LOWER(COALESCE(n.requestNumber, '')) LIKE :searchPattern
                   OR LOWER(COALESCE(n.requestType, '')) LIKE :searchPattern)
            """)
    Page<UserNotificationEntity> findForUser(
            @Param("userId") UUID userId,
            @Param("read") Boolean read,
            @Param("excludeCompanyNotice") boolean excludeCompanyNotice,
            @Param("searchPattern") String searchPattern,
            Pageable pageable);

    long countByUserIdAndReadFalseAndArchivedFalse(UUID userId);

    @Query("""
            SELECT COUNT(n) FROM UserNotificationEntity n
            WHERE n.userId = :userId
              AND n.read = false
              AND n.archived = false
              AND (n.category IS NULL OR n.category <> 'COMPANY_NOTICE')
            """)
    long countWorkflowUnread(@Param("userId") UUID userId);

    List<UserNotificationEntity> findByUserIdAndReadFalseAndArchivedFalse(UUID userId);

    Optional<UserNotificationEntity> findByIdAndUserId(UUID id, UUID userId);
}
