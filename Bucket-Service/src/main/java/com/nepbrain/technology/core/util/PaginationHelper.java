package com.nepbrain.technology.core.util;

import com.nepbrain.technology.core.payload.request.PaginationRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public class PaginationHelper {

    public static Pageable getPageable(PaginationRequest request) {
        int pageNo = Optional.ofNullable(request.getPageNo()).orElse(Constant.DEFAULT_PAGE_NO);
        int pageSize = Optional.ofNullable(request.getPageSize()).orElse(Constant.DEFAULT_PAGE_SIZE);
        String sortBy = Optional.ofNullable(request.getSortBy()).filter(s -> !s.isBlank()).orElse(Constant.DEFAULT_SORT_BY);
        Sort.Direction direction = Constant.DEFAULT_SORT_DIRECTION.equalsIgnoreCase(request.getSortDirection()) ? Sort.Direction.DESC :
                Sort.Direction.ASC;
        return PageRequest.of(pageNo, pageSize, Sort.by(direction, sortBy));
    }
}
