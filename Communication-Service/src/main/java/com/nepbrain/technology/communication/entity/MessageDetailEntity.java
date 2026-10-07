package com.nepbrain.technology.communication.entity;

import com.nepbrain.technology.communication.enums.LanguageEnum;
import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/*
 * @Created At 03/03/2024
 * @Author suroj.awal
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "message_detail")
@Builder
public class MessageDetailEntity extends BaseAuditEntity {

    @Enumerated(EnumType.STRING)
    private LanguageEnum language;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "message_id")
    private MessageEntity message;

    @Column(name = "email_subject", columnDefinition = "text")
    private String emailSubject;

    @Column(name = "email_body", columnDefinition = "text")
    private String emailBody;

    @Column(name = "sms_body", columnDefinition = "text")
    private String smsBody;

    @Column(name = "notification_subject", columnDefinition = "text")
    private String notificationSubject;

    @Column(name = "notification_body", columnDefinition = "text")
    private String notificationBody;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MessageDetailEntity that = (MessageDetailEntity) o;
        return language == that.language && Objects.equals(message, that.message)
                && Objects.equals(emailSubject, that.emailSubject) && Objects.equals(emailBody, that.emailBody)
                && Objects.equals(smsBody, that.smsBody)
                && Objects.equals(notificationSubject, that.notificationSubject)
                && Objects.equals(notificationBody, that.notificationBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), language);
    }
}
