package com.nepbrain.technology.customer.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDetailRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

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

    private String additionalInfo;

    private String profilePictureUrl;
}

