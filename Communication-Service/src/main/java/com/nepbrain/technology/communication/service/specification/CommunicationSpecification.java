package com.nepbrain.technology.communication.service.specification;

import com.nepbrain.technology.communication.entity.CommunicationEntity;
import com.nepbrain.technology.communication.enums.ErrorCodeEnum;
import com.nepbrain.technology.communication.payload.request.CommunicationSearchFilterPaginationRequest;
import com.nepbrain.technology.communication.util.Constant;
import com.nepbrain.technology.core.exception.GlobalException;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommunicationSpecification {
    private CommunicationSpecification() {
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.DEFAULT_DATE);

    public static Specification<CommunicationEntity> getCommunications(
            CommunicationSearchFilterPaginationRequest request) {

        return ((root, query, criteriaBuilder) -> {
            Predicate finalPredicate = criteriaBuilder.conjunction();

            //Search
            if (StringUtils.isNotEmpty(request.getSearchText())) {
                Predicate searchTextPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root
                                .get("username")), likePattern(request.getSearchText()
                                .toLowerCase())));
                finalPredicate = criteriaBuilder.and(searchTextPredicate, finalPredicate);
            }

            //Status Filter
            if (ObjectUtils.isNotEmpty(request.getStatus())) {
                Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), request.getStatus());
                finalPredicate = criteriaBuilder.and(statusPredicate, finalPredicate);
            }

            //Channel Filter
            if (ObjectUtils.isNotEmpty(request.getType())) {
                Predicate typePredicate = criteriaBuilder.equal(root.get("type"), request.getType());
                finalPredicate = criteriaBuilder.and(typePredicate, finalPredicate);
            }

            //Subject Filter
            if (ObjectUtils.isNotEmpty(request.getTopic())) {
                Predicate subjectPredicate = criteriaBuilder.equal(root.get("topic"), request.getTopic());
                finalPredicate = criteriaBuilder.and(subjectPredicate, finalPredicate);
            }

            //Language Filter
            if (ObjectUtils.isNotEmpty(request.getLanguage())) {
                Predicate languagePredicate = criteriaBuilder.equal(root.get("language"), request.getLanguage());
                finalPredicate = criteriaBuilder.and(languagePredicate, finalPredicate);
            }

            //Date Range Filter
            if (request.getFromDate() != null && request.getToDate() != null) {
                LocalDateTime from = LocalDateTime.parse(request.getFromDate(), formatter);
                LocalDateTime to = LocalDateTime.parse(request.getToDate(), formatter);
                if (to.isBefore(from)) {
                    throw new GlobalException(ErrorCodeEnum.DAT001.name());
                }
                Predicate createdDatePredicate = criteriaBuilder.between(root.get(Constant.CREATED_AT), from, to);
                finalPredicate = criteriaBuilder.and(finalPredicate, createdDatePredicate);
            }

            return finalPredicate;
        });
    }

    private static String likePattern(String value) {
        return "%" + value + "%";
    }

}
