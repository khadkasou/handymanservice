package com.nepbrain.technology.communication.entity;

/*
 * @Created At 12/03/2024
 * @Author suroj.awal
 */

import com.nepbrain.technology.communication.enums.ChannelEnum;
import com.nepbrain.technology.communication.enums.LanguageEnum;
import com.nepbrain.technology.communication.enums.StatusEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.Objects;

@Entity(name = "communication")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunicationEntity extends BaseAuditEntity {

    private String username;
    private String userId;
    @Enumerated(EnumType.STRING)
    private TopicEnum topic;
    @Enumerated(EnumType.STRING)
    private ChannelEnum type;
    private String subject;
    @Column(columnDefinition = "text")
    private String message;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    @Enumerated(EnumType.STRING)
    private LanguageEnum language;

    @Column(columnDefinition = "text")
    private String response;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CommunicationEntity that = (CommunicationEntity) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId);
    }
}
