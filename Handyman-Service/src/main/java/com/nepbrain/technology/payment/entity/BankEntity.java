package com.nepbrain.technology.payment.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import com.nepbrain.technology.core.enums.StatusEnum;
import lombok.*;

import jakarta.persistence.*;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hm_bank")
public class BankEntity extends BaseAuditEntity {

    private UUID userId;
    private String bankName;
    private String branchName;
    private String accountNumber;
    private String ifscNo;
    private String accountHolderName;
    private Boolean isDefault;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;
}
