package com.nepbrain.technology.provider.repository;

import com.nepbrain.technology.core.repsoitory.BaseRepository;
import com.nepbrain.technology.provider.entity.FavouriteProviderEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavouriteProviderRepository extends BaseRepository<FavouriteProviderEntity, UUID> {

    List<FavouriteProviderEntity> findByUserId(UUID userId);

    Optional<FavouriteProviderEntity> findByUserIdAndProviderId(UUID userId, UUID providerId);

    void deleteByUserIdAndProviderId(UUID userId, UUID providerId);
}
