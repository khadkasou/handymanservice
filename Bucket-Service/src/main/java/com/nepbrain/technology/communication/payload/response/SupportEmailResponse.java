package com.nepbrain.technology.communication.payload.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportEmailResponse {

    private UUID id;
    private String email;
    private String name;
    private String mobileNumber;
    private Boolean isCompany;
    private String companyName;
    private String address;
    private String subject;
    private String message;
    private LocalDateTime createdAt;
    private List<SupportEmailReplyResponse> replies;
}
