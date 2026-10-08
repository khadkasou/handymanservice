package com.nepbrain.technology.auth.payload.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    private Boolean status;
    private String message;
    private UserResponse userData;
}
