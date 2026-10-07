package com.nepbrain.technology.payment.repository;
import com.nepbrain.technology.payment.entity.PaymentEntity; import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository; import java.util.List; import java.util.UUID;
@Repository public interface PaymentRepository extends BaseRepository<PaymentEntity, UUID> {
    List<PaymentEntity> findByBookingId(UUID bookingId);
}
