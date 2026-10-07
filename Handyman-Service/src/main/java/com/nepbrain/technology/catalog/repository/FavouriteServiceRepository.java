package com.nepbrain.technology.catalog.repository;
import com.nepbrain.technology.catalog.entity.FavouriteServiceEntity; import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository; import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository public interface FavouriteServiceRepository extends BaseRepository<FavouriteServiceEntity, UUID> {
    List<FavouriteServiceEntity> findByUserId(UUID userId);
    Optional<FavouriteServiceEntity> findByUserIdAndServiceId(UUID userId, UUID serviceId);
    void deleteByUserIdAndServiceId(UUID userId, UUID serviceId);
}
