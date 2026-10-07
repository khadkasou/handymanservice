package com.nepbrain.technology.communication.repository;

import com.nepbrain.technology.communication.entity.MessageDetailEntity;
import com.nepbrain.technology.communication.entity.MessageEntity;
import com.nepbrain.technology.communication.enums.LanguageEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
 * @Created At 04/03/2024
 * @Author suroj.awal
 */
@Repository
public interface MessageDetailRepository extends JpaRepository<MessageDetailEntity, UUID> {

    MessageDetailEntity findByMessage(MessageEntity message);

    List<MessageDetailEntity> findAllByMessage(MessageEntity message);

    Optional<MessageDetailEntity> findByMessageAndLanguage(MessageEntity message, LanguageEnum language);

}
