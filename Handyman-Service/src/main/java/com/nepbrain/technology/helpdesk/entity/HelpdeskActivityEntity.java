package com.nepbrain.technology.helpdesk.entity;
import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.*; import lombok.*; import java.util.UUID;
@Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "hm_helpdesk_activity")
public class HelpdeskActivityEntity extends BaseAuditEntity {
    private UUID helpdeskId; private UUID senderId; @Column(length = 4000) private String messages;
    private String senderType; private String activityType;
}
