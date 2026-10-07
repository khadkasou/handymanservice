package com.nepbrain.technology.customer.mapper;

import com.nepbrain.technology.customer.entity.CustomerEntity;
import com.nepbrain.technology.customer.payload.request.CustomerRequest;
import com.nepbrain.technology.customer.payload.response.CustomerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(target = "customerDetail", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "bankDetails", ignore = true)
    @Mapping(target = "preferences", ignore = true)
    CustomerEntity toEntity(CustomerRequest request);
    
    @Mapping(target = "customerDetail", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "bankDetails", ignore = true)
    @Mapping(target = "organizationName", ignore = true)
    @Mapping(target = "branchName", ignore = true)
    CustomerResponse toResponse(CustomerEntity entity);
    
    @Mapping(target = "customerDetail", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "bankDetails", ignore = true)
    @Mapping(target = "preferences", ignore = true)
    void updateEntity(CustomerRequest request, @MappingTarget CustomerEntity entity);
}

