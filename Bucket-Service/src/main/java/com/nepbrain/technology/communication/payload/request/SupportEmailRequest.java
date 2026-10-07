package com.nepbrain.technology.communication.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportEmailRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;

    private String mobileNumber;

    @NotNull
    private Boolean isCompany;

    private String companyName;
    private String address;
    private String subject;
    private String message;
}
