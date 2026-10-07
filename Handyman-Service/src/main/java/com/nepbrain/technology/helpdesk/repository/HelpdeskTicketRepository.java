package com.nepbrain.technology.helpdesk.repository;
import com.nepbrain.technology.helpdesk.entity.HelpdeskTicketEntity; import com.nepbrain.technology.core.repsoitory.BaseRepository;
import org.springframework.stereotype.Repository; import java.util.List; import java.util.UUID;
@Repository public interface HelpdeskTicketRepository extends BaseRepository<HelpdeskTicketEntity, UUID> {
    List<HelpdeskTicketEntity> findByCustomerId(UUID customerId);
    List<HelpdeskTicketEntity> findByStatus(String status);
}
