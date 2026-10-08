package com.nepbrain.technology.booking.repository;

import com.nepbrain.technology.booking.entity.BookingEntity;
import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends BaseRepository<BookingEntity, UUID> {

    List<BookingEntity> findByCustomerId(UUID customerId);
}
