package com.nepbrain.technology.customer.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customer_detail")
public class CustomerDetailEntity extends BaseAuditEntity {

    private String companyName;

    private String taxId;

    private String registrationNumber;

    private String website;

    private String industry;

    private String customerType; // INDIVIDUAL, BUSINESS, CORPORATE

    private String contactPersonName;

    private String contactPersonPhone;

    private String contactPersonEmail;

    private String creditLimit;

    private String paymentTerms;

    private String preferredCurrency;

    private String preferredLanguage;

    @Column(length = 500)
    private String additionalInfo;

    private String profilePictureUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private CustomerEntity customer;
}

