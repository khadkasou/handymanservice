package com.nepbrain.technology.geo.repository;
import com.nepbrain.technology.geo.entity.StateEntity; import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository; import java.util.List; import java.util.UUID;
@Repository public interface StateRepository extends BaseRepository<StateEntity, UUID> {
    List<StateEntity> findByCountryId(UUID countryId);
}
