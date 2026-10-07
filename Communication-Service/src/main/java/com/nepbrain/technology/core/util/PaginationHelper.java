package com.nepbrain.technology.core.util;


import com.nepbrain.technology.core.payload.PaginationRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static com.nepbrain.technology.core.util.Constant.*;

public class PaginationHelper {

    public static Pageable getPageable(PaginationRequest request) {
        int pageNo = Optional.ofNullable(request.getPageNo()).orElse(DEFAULT_PAGE_NO);
        int pageSize = Optional.ofNullable(request.getPageSize()).orElse(DEFAULT_PAGE_SIZE);
        String sortBy = Optional.ofNullable(request.getSortBy()).filter(s -> !s.isBlank()).orElse(DEFAULT_SORT_BY);
        Sort.Direction direction = DEFAULT_SORT_DIRECTION.equalsIgnoreCase(request.getSortDirection()) ? Sort.Direction.DESC :
                Sort.Direction.ASC;
        return PageRequest.of(pageNo, pageSize, Sort.by(direction, sortBy));
    }
}
