package com.nepbrain.technology.communication.repository;

import com.nepbrain.technology.communication.entity.OTPEntity;
import com.nepbrain.technology.communication.enums.OTPStatusEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
 * @Created At 04/03/2024
 * @Author suroj.awal
 */
@Repository
public interface OTPRepository extends JpaRepository<OTPEntity, String> {

    OTPEntity findByUsername(String username);

    OTPEntity findByUsernameAndTopic(String email, String topic);


    OTPEntity findByUsernameAndTopicAndStatus(String username, TopicEnum topic, OTPStatusEnum status);

    OTPEntity findFirstByUsernameAndTopicOrderByCreatedAtDesc(String username, TopicEnum topic);
}
