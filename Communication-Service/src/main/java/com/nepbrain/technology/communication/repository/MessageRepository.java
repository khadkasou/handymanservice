package com.nepbrain.technology.communication.repository;

import com.nepbrain.technology.communication.entity.MessageEntity;
import com.nepbrain.technology.communication.enums.StatusEnum;
import com.nepbrain.technology.communication.enums.TopicEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/*
 * @Created At 04/03/2024
 * @Author suroj.awal
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, UUID>, JpaSpecificationExecutor<MessageEntity> {

    MessageEntity findByTopic(TopicEnum topic);
    boolean existsByTopic(TopicEnum topic);

    long countByStatus(StatusEnum status);

    long countByCreatedAtGreaterThanEqual(LocalDateTime from);

    List<MessageEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
            SELECT COUNT(m) FROM message m
            WHERE m.createdAt >= :from AND m.createdAt < :to
            """)
    long countCreatedBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
