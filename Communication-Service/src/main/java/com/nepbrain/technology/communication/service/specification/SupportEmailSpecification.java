package com.nepbrain.technology.communication.service.specification;

import com.nepbrain.technology.communication.entity.SupportEmailEntity;
import com.nepbrain.technology.communication.payload.request.SupportEmailDataRequest;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SupportEmailSpecification {

    private SupportEmailSpecification() {
    }

    public static Specification<SupportEmailEntity> supportEmailFilter(SupportEmailDataRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(request.getEmail())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                        "%" + request.getEmail().toLowerCase() + "%"));
            }
            if (StringUtils.isNotBlank(request.getName())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + request.getName().toLowerCase() + "%"));
            }
            if (request.getIsCompany() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isCompany"), request.getIsCompany()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
