package com.nepbrain.technology.provider.repository;

import com.nepbrain.technology.core.repsoitory.BaseRepository;
import com.nepbrain.technology.provider.entity.HandymanRatingEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HandymanRatingRepository extends BaseRepository<HandymanRatingEntity, UUID> {

    List<HandymanRatingEntity> findByHandymanId(UUID handymanId);
}
