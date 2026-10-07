package com.nepbrain.technology.customer.payload.response;

import com.nepbrain.technology.core.enums.CountryEnum;
import com.nepbrain.technology.customer.enums.AddressTypeEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerAddressResponse {
    private UUID id;
    private UUID customerId;
    private String customerName;
    private AddressTypeEnum addressType;
    private String streetAddress;
    private String streetAddress2;
    private UUID cityId;
    private String cityName;
    private String postalCode;
    private CountryEnum country;
    private String latitude;
    private String longitude;
    private String additionalInfo;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private UUID createdBy;
    private UUID lastModifiedBy;
}

