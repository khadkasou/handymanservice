package com.nepbrain.technology.booking.repository;

import com.nepbrain.technology.booking.entity.CouponEntity;
import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CouponRepository extends BaseRepository<CouponEntity, UUID> {

    List<CouponEntity> findByServiceId(UUID serviceId);
}
