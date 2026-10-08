package com.nepbrain.technology.customer.mapper;

import com.nepbrain.technology.customer.entity.CustomerPreferenceEntity;
import com.nepbrain.technology.customer.payload.request.CustomerPreferenceRequest;
import com.nepbrain.technology.customer.payload.response.CustomerPreferenceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerPreferenceMapper {
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "status", ignore = true)
    CustomerPreferenceEntity toEntity(CustomerPreferenceRequest request);
    
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", expression = "java(entity.getCustomer().getFirstName() + \" \" + entity.getCustomer().getLastName())")
    CustomerPreferenceResponse toResponse(CustomerPreferenceEntity entity);
    
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(CustomerPreferenceRequest request, @MappingTarget CustomerPreferenceEntity entity);
}

