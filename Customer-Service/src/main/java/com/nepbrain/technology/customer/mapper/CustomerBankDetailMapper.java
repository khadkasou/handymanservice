package com.nepbrain.technology.customer.mapper;

import com.nepbrain.technology.customer.entity.CustomerBankDetailEntity;
import com.nepbrain.technology.customer.payload.request.CustomerBankDetailRequest;
import com.nepbrain.technology.customer.payload.response.CustomerBankDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerBankDetailMapper {
    @Mapping(target = "customer", ignore = true)
    CustomerBankDetailEntity toEntity(CustomerBankDetailRequest request);
    
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", expression = "java(entity.getCustomer().getFirstName() + \" \" + entity.getCustomer().getLastName())")
    CustomerBankDetailResponse toResponse(CustomerBankDetailEntity entity);
    
    @Mapping(target = "customer", ignore = true)
    void updateEntity(CustomerBankDetailRequest request, @MappingTarget CustomerBankDetailEntity entity);
}

