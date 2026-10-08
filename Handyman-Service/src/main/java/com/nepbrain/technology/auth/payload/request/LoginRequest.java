package com.nepbrain.technology.auth.payload.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    private String email;
    private String username;
    private String password;
    private String loginType;
    private String uid;
    private String firstName;
    private String lastName;
    private String profileImage;
    private String contactNumber;
}
