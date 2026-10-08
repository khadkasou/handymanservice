package com.nepbrain.technology.auth.payload.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileRequest {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private UUID countryId;
    private UUID stateId;
    private UUID cityId;
    private String address;
    private String profileImage;
    private String displayName;
}
