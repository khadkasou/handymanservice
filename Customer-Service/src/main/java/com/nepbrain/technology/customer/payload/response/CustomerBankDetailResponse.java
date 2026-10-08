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
public class CustomerBankDetailResponse {
    private UUID id;
    private UUID customerId;
    private String customerName;
    private String bankName;
    private String accountNumber;
    private String accountHolderName;
    private String branchName;
    private String branchAddress;
    private String ifscCode;
    private String swiftCode;
    private String accountType;
    private Boolean isPrimary;
    private StatusEnum status;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private UUID createdBy;
    private UUID lastModifiedBy;
}

