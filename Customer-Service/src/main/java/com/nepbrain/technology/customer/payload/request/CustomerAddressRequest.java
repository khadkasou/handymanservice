package com.nepbrain.technology.customer.payload.request;

import com.nepbrain.technology.core.enums.CountryEnum;
import com.nepbrain.technology.customer.enums.AddressTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerAddressRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Address type is required")
    private AddressTypeEnum addressType;

    @NotBlank(message = "Street address is required")
    private String streetAddress;

    private String streetAddress2;

    private UUID cityId;

    private String postalCode;

    @NotNull(message = "Country is required")
    private CountryEnum country;

    private String latitude;

    private String longitude;

    private String additionalInfo;

    private Boolean isPrimary;
}

