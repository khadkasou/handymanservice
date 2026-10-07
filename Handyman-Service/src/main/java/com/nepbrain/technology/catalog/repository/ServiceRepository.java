package com.nepbrain.technology.catalog.repository;
import com.nepbrain.technology.catalog.entity.ServiceEntity; import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository; import java.util.List; import java.util.UUID;
@Repository public interface ServiceRepository extends BaseRepository<ServiceEntity, UUID> {
    List<ServiceEntity> findByCategoryId(UUID categoryId);
    List<ServiceEntity> findByNameContainingIgnoreCase(String name);
}
