package com.nepbrain.technology.customer.mapper;

import com.nepbrain.technology.customer.entity.CustomerDetailEntity;
import com.nepbrain.technology.customer.payload.request.CustomerDetailRequest;
import com.nepbrain.technology.customer.payload.response.CustomerDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerDetailMapper {
    @Mapping(target = "customer", ignore = true)
    CustomerDetailEntity toEntity(CustomerDetailRequest request);
    
    @Mapping(target = "customerId", source = "customer.id")
    CustomerDetailResponse toResponse(CustomerDetailEntity entity);
    
    @Mapping(target = "customer", ignore = true)
    void updateEntity(CustomerDetailRequest request, @MappingTarget CustomerDetailEntity entity);
}

