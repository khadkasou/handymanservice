package com.nepbrain.technology.auth.payload.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String userType;
    private UUID countryId;
    private UUID stateId;
    private UUID cityId;
    private String address;
    private String loginType;
    private String uid;
}
