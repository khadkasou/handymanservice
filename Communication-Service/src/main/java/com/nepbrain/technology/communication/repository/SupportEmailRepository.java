package com.nepbrain.technology.communication.repository;

import com.nepbrain.technology.communication.entity.SupportEmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SupportEmailRepository extends JpaRepository<SupportEmailEntity, UUID>, JpaSpecificationExecutor<SupportEmailEntity> {

    long countByCreatedAtGreaterThanEqual(LocalDateTime from);

    @Query("SELECT COUNT(s) FROM support_email s WHERE SIZE(s.replies) = 0")
    long countOpenWithoutReplies();

    List<SupportEmailEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
            SELECT COUNT(s) FROM support_email s
            WHERE s.createdAt >= :from AND s.createdAt < :to
            """)
    long countCreatedBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
