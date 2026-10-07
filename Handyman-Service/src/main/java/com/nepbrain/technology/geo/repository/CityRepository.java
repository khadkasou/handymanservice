package com.nepbrain.technology.geo.repository;
import com.nepbrain.technology.geo.entity.CityEntity; import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository; import java.util.List; import java.util.UUID;
@Repository public interface CityRepository extends BaseRepository<CityEntity, UUID> {
    List<CityEntity> findByStateId(UUID stateId);
}
