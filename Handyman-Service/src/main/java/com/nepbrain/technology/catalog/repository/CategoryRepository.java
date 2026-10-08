package com.nepbrain.technology.catalog.repository;

import com.nepbrain.technology.catalog.entity.CategoryEntity;
import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends BaseRepository<CategoryEntity, UUID> {

    List<CategoryEntity> findByParentIdIsNull();

    List<CategoryEntity> findByParentId(UUID parentId);
}
