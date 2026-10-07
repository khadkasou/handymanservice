package com.nepbrain.technology.communication.repository;

import com.nepbrain.technology.communication.entity.MessageEntity;
import com.nepbrain.technology.communication.enums.TopicEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/*
 * @Created At 04/03/2024
 * @Author suroj.awal
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, UUID>, JpaSpecificationExecutor<MessageEntity> {

    MessageEntity findByTopic(TopicEnum topic);
    boolean existsByTopic(TopicEnum topic);

}
