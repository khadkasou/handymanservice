package com.nepbrain.technology.communication.entity;

import com.nepbrain.technology.communication.enums.StatusEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import com.nepbrain.technology.core.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

/*
 * @Created At 03/03/2024
 * @Author suroj.awal
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "message")
@Builder
public class MessageEntity extends BaseAuditEntity {

    @Enumerated(EnumType.STRING)
    private TopicEnum topic;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    private Boolean enableSms;
    private Boolean enableEmail;
    private Boolean enableNotification;
    @OneToMany(mappedBy = "message", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    private List<MessageDetailEntity> messages;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MessageEntity that = (MessageEntity) o;
        return topic == that.topic;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), topic);
    }
}
