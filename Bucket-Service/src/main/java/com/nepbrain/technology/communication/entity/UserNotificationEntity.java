package com.nepbrain.technology.communication.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_notification", indexes = {
        @Index(name = "idx_user_notification_user_read", columnList = "user_id, is_read, is_archived"),
        @Index(name = "uk_user_notification_idempotency", columnList = "workflow_history_id, user_id, notification_type", unique = true)
})
public class UserNotificationEntity extends BaseAuditEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "recipient_employee_id")
    private UUID recipientEmployeeId;

    @Column(name = "company_id")
    private UUID companyId;

    @Column(name = "branch_id")
    private UUID branchId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean read = false;

    @Column(length = 100)
    private String topic;

    @Column(name = "notification_type", length = 80)
    private String notificationType;

    @Column(name = "category", length = 40)
    private String category;

    @Column(name = "reference_type", length = 100)
    private String referenceType;

    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column(name = "request_type", length = 40)
    private String requestType;

    @Column(name = "request_id")
    private UUID requestId;

    @Column(name = "request_number", length = 100)
    private String requestNumber;

    @Column(name = "workflow_history_id")
    private UUID workflowHistoryId;

    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @Column(name = "action_label", length = 100)
    private String actionLabel;

    @Column(name = "priority", length = 20)
    private String priority;

    @Column(name = "channel", length = 20)
    private String channel;

    @Column(name = "is_archived", nullable = false)
    @Builder.Default
    private boolean archived = false;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "metadata", columnDefinition = "text")
    private String metadata;
}
