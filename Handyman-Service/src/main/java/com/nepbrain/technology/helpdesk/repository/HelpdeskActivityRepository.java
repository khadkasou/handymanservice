package com.nepbrain.technology.helpdesk.repository;
import com.nepbrain.technology.helpdesk.entity.HelpdeskActivityEntity; import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository; import java.util.List; import java.util.UUID;
@Repository public interface HelpdeskActivityRepository extends BaseRepository<HelpdeskActivityEntity, UUID> {
    List<HelpdeskActivityEntity> findByHelpdeskId(UUID helpdeskId);
}
