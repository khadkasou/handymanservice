package com.nepbrain.technology.customer.mapper;

import com.nepbrain.technology.customer.entity.CustomerAddressEntity;
import com.nepbrain.technology.customer.payload.request.CustomerAddressRequest;
import com.nepbrain.technology.customer.payload.response.CustomerAddressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerAddressMapper {
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "status", ignore = true)
    CustomerAddressEntity toEntity(CustomerAddressRequest request);
    
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", expression = "java(entity.getCustomer().getFirstName() + \" \" + entity.getCustomer().getLastName())")
    @Mapping(target = "cityName", ignore = true)
    CustomerAddressResponse toResponse(CustomerAddressEntity entity);
    
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(CustomerAddressRequest request, @MappingTarget CustomerAddressEntity entity);
}

