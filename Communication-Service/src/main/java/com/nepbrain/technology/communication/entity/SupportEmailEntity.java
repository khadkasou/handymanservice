package com.nepbrain.technology.communication.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Support / contact email for communication (email, name, phone, subject, message; optional company name and address).
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "support_email")
@Builder
@Table(indexes = @Index(name = "idx_support_email_email", columnList = "email"))
public class SupportEmailEntity extends BaseAuditEntity {

    @Column(nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "is_company", nullable = false)
    private Boolean isCompany;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "address", columnDefinition = "text")
    private String address;

    @Column(name = "subject")
    private String subject;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @OneToMany(mappedBy = "supportEmail", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SupportEmailReplyEntity> replies = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SupportEmailEntity that = (SupportEmailEntity) o;
        return Objects.equals(email, that.email)
                && Objects.equals(name, that.name)
                && Objects.equals(mobileNumber, that.mobileNumber)
                && Objects.equals(isCompany, that.isCompany)
                && Objects.equals(companyName, that.companyName)
                && Objects.equals(address, that.address)
                && Objects.equals(subject, that.subject)
                && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), email);
    }
}
