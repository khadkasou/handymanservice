package com.nepbrain.technology.blog.repository;

import com.nepbrain.technology.blog.entity.BlogEntity;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BlogRepository extends BaseRepository<BlogEntity, UUID> {

    List<BlogEntity> findByStatus(StatusEnum status);
}
