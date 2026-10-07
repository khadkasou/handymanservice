package com.nepbrain.technology.booking.repository;
import com.nepbrain.technology.booking.entity.BookingStatusEntity; import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository; import java.util.UUID;
@Repository public interface BookingStatusRepository extends BaseRepository<BookingStatusEntity, UUID> {}
