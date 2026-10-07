package com.nepbrain.technology.communication.service.specification;

import com.nepbrain.technology.communication.entity.MessageEntity;
import com.nepbrain.technology.communication.payload.request.MessageDataRequest;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/*
 * @Created At 03/03/2024
 * @Author suroj.awal
 */
public class MessageSpecification {

    private MessageSpecification() {
    }

    public static Specification<MessageEntity> messageFilter(MessageDataRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (ObjectUtils.isNotEmpty(request.getTopic())) {
                predicates.add(criteriaBuilder.equal(root.get("topic"), request.getTopic()));
            }
            if (ObjectUtils.isNotEmpty(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
