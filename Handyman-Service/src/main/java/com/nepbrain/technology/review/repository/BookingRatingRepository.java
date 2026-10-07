package com.nepbrain.technology.review.repository;
import com.nepbrain.technology.review.entity.BookingRatingEntity; import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository; import java.util.List; import java.util.UUID;
@Repository public interface BookingRatingRepository extends BaseRepository<BookingRatingEntity, UUID> {
    List<BookingRatingEntity> findByServiceId(UUID serviceId);
    List<BookingRatingEntity> findByCustomerId(UUID customerId);
}
