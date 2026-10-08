package com.nepbrain.technology.customer.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import com.nepbrain.technology.core.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customer_bank_detail")
public class CustomerBankDetailEntity extends BaseAuditEntity {

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String accountNumber;

    private String accountHolderName;

    private String branchName;

    private String branchAddress;

    private String ifscCode;

    private String swiftCode;

    private String accountType; // SAVINGS, CURRENT, FIXED_DEPOSIT, etc.

    private Boolean isPrimary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20) default 'ACTIVE'")
    private StatusEnum status = StatusEnum.ACTIVE;

    @Column(length = 500)
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;
}

