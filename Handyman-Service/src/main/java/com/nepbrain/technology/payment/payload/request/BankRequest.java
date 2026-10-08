package com.nepbrain.technology.payment.payload.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankRequest {

    private UUID id;
    private UUID userId;
    private String bankName;
    private String branchName;
    private String accountNumber;
    private String ifscNo;
    private String accountHolderName;
    private Boolean isDefault;
}
