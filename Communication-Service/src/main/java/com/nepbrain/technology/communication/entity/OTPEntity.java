package com.nepbrain.technology.communication.entity;

/*
 * @Created At 12/03/2024
 * @Author suroj.awal
 */

import com.nepbrain.technology.communication.enums.OTPStatusEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import lombok.*;

import java.util.Objects;

@Entity(name = "otp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OTPEntity extends BaseAuditEntity {

    private String username;
    private String email;
    private String code;
    @Enumerated(EnumType.STRING)
    private TopicEnum topic;

    @Enumerated(EnumType.STRING)
    private OTPStatusEnum status;
    private String userId;

    @PrePersist
    private void init() {
        status = OTPStatusEnum.PENDING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OTPEntity otpEntity = (OTPEntity) o;
        return Objects.equals(username, otpEntity.username) && Objects.equals(email, otpEntity.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, email);
    }
}
