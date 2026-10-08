package com.nepbrain.technology.customer.payload.response;

import com.nepbrain.technology.core.enums.StatusEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDetailResponse {
    private UUID id;
    private UUID customerId;
    private String companyName;
    private String taxId;
    private String registrationNumber;
    private String website;
    private String industry;
    private String customerType;
    private String contactPersonName;
    private String contactPersonPhone;
    private String contactPersonEmail;
    private String creditLimit;
    private String paymentTerms;
    private String preferredCurrency;
    private String preferredLanguage;
    private String additionalInfo;
    private String profilePictureUrl;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private UUID createdBy;
    private UUID lastModifiedBy;
}

