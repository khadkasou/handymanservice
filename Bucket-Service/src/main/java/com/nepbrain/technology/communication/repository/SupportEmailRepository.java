package com.nepbrain.technology.communication.repository;

import com.nepbrain.technology.communication.entity.SupportEmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SupportEmailRepository extends JpaRepository<SupportEmailEntity, UUID>, JpaSpecificationExecutor<SupportEmailEntity> {
}
