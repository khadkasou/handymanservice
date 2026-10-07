package com.nepbrain.technology.communication.repository;

import com.nepbrain.technology.communication.entity.SupportEmailEntity;
import com.nepbrain.technology.communication.entity.SupportEmailReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SupportEmailReplyRepository extends JpaRepository<SupportEmailReplyEntity, UUID> {

    List<SupportEmailReplyEntity> findAllBySupportEmailOrderByCreatedAtAsc(SupportEmailEntity supportEmail);
}
