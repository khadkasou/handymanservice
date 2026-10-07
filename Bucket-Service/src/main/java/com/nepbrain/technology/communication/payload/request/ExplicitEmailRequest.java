package com.nepbrain.technology.communication.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ExplicitEmailRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String subject;

    @NotBlank
    private String body;

    private UUID companyId;
    private String username;
}
