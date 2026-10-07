package com.nepbrain.technology.communication.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/*
 * Reply to a support email (OneToMany from SupportEmailEntity).
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "support_email_reply")
@Builder
public class SupportEmailReplyEntity extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_email_id", nullable = false)
    private SupportEmailEntity supportEmail;

    @Column(name = "message", columnDefinition = "text", nullable = false)
    private String message;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SupportEmailReplyEntity that = (SupportEmailReplyEntity) o;
        return Objects.equals(supportEmail, that.supportEmail)
                && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), supportEmail != null ? supportEmail.getId() : null);
    }
}
