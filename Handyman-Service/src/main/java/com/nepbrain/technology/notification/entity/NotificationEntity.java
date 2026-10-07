package com.nepbrain.technology.notification.entity;
import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.*; import lombok.*; import java.util.UUID;
@Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "hm_notification")
public class NotificationEntity extends BaseAuditEntity {
    private UUID customerId; private String type; private String subject;
    @Column(length = 4000) private String message; private Boolean isRead; private UUID dataId;
}
