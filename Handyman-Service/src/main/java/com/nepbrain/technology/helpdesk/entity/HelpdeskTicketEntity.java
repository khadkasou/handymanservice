package com.nepbrain.technology.helpdesk.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import lombok.*;

import jakarta.persistence.*;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hm_helpdesk_ticket")
public class HelpdeskTicketEntity extends BaseAuditEntity {

    private UUID customerId;
    private String subject;

    @Column(length = 4000)
    private String description;

    private String status;
    private String mode;
    private String type;
}
