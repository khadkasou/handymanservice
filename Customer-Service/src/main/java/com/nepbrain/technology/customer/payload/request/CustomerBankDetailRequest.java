package com.nepbrain.technology.customer.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerBankDetailRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    private String accountHolderName;

    private String branchName;

    private String branchAddress;

    private String ifscCode;

    private String swiftCode;

    private String accountType; // SAVINGS, CURRENT, FIXED_DEPOSIT, etc.

    private Boolean isPrimary;

    private String remarks;
}

