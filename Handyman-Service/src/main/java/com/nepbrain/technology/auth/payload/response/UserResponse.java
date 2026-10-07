package com.nepbrain.technology.auth.payload.response;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class UserResponse {

    private UUID id; private String email; private String username; private String firstName; private String lastName;
    private String contactNumber; private String userType; private String status; private String profileImage;
    private UUID countryId; private UUID stateId; private UUID cityId; private String address; private String uid;
    private String loginType; private Boolean emailVerified; private String apiToken; private String displayName;
    private String designation; private Boolean isFeatured; private UUID providerId;
}
