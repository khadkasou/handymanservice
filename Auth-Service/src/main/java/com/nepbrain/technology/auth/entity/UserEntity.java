package com.nepbrain.technology.auth.entity;

import com.nepbrain.technology.auth.enums.UserStatusEnum;
import com.nepbrain.technology.auth.enums.UserTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"email_address", "mobile_number"}))
public class UserEntity extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String emailAddress;

    @Column(length = 20)
    private String mobileNumber;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_account_non_expired", nullable = false)
    @Builder.Default
    private boolean accountNonExpired = true;

    @Column(name = "is_account_non_locked", nullable = false)
    @Builder.Default
    private boolean accountNonLocked = true;

    @Column(name = "is_credentials_non_expired", nullable = false)
    @Builder.Default
    private boolean credentialsNonExpired = true;

    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private UserStatusEnum status = UserStatusEnum.ACTIVE;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private List<RoleEntity> roles = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "user_type")
    @Builder.Default
    private List<UserTypeEnum> userType = new ArrayList<>();

    @Column(name = "auth_provider")
    private String authProvider;

}
