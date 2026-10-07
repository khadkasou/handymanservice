package com.nepbrain.technology.postjob.repository;
import com.nepbrain.technology.postjob.entity.PostJobEntity; import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository; import java.util.List; import java.util.UUID;
@Repository public interface PostJobRepository extends BaseRepository<PostJobEntity, UUID> {
    List<PostJobEntity> findByCustomerId(UUID customerId);
}
