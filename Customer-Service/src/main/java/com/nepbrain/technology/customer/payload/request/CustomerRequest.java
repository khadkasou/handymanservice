package com.nepbrain.technology.customer.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerRequest {

    @NotBlank(message = "Customer code is required")
    private String customerCode;

    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String alternatePhoneNumber;

    private LocalDate dateOfBirth;

    private String notes;

    private UUID organizationId;

    private UUID branchId;
}

