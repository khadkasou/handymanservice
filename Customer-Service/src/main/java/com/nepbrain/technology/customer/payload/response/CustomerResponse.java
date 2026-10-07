package com.nepbrain.technology.customer.payload.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {
    private UUID id;
    private String customerCode;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String alternatePhoneNumber;
    private LocalDate dateOfBirth;
    private String notes;
    private UUID organizationId;
    private String organizationName;
    private UUID branchId;
    private String branchName;
    private CustomerDetailResponse customerDetail;
    private List<CustomerAddressResponse> addresses;
    private List<CustomerBankDetailResponse> bankDetails;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private UUID createdBy;
    private UUID lastModifiedBy;
}

