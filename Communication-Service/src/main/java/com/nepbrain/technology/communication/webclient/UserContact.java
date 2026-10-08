package com.nepbrain.technology.communication.webclient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContact {
    private String id;
    private String email;
    private String mobileNumber;
    private String fullName;
    private List<String> fcmTokens;
}
