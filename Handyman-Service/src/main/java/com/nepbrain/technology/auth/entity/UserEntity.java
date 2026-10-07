package com.nepbrain.technology.auth.entity;
import com.nepbrain.technology.auth.enums.UserTypeEnum;
import com.nepbrain.technology.core.entity.BaseAuditEntity;
import com.nepbrain.technology.core.enums.StatusEnum;
import jakarta.persistence.*; import lombok.*; import java.util.UUID;
@Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "hm_user")
public class UserEntity extends BaseAuditEntity {
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false, unique = true) private String username;
    @Column(nullable = false) private String passwordHash;
    private String firstName; private String lastName; private String contactNumber;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private UserTypeEnum userType;
    @Enumerated(EnumType.STRING) private StatusEnum status;
    private String profileImage; private UUID countryId; private UUID stateId; private UUID cityId;
    private String address; private String uid; private String loginType; private Boolean emailVerified;
    private String apiToken; private String displayName; private String designation;
    private String description; private Boolean isFeatured; private UUID providerId;
}
