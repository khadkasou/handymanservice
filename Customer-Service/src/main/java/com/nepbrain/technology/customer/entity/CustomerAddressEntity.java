package com.nepbrain.technology.customer.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import com.nepbrain.technology.core.enums.CountryEnum;
import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.customer.enums.AddressTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customer_address")
public class CustomerAddressEntity extends BaseAuditEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AddressTypeEnum addressType;

    @Column(nullable = false)
    private String streetAddress;

    private String streetAddress2;

    private UUID cityId;

    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CountryEnum country;

    private String latitude;

    private String longitude;

    @Column(columnDefinition = "TEXT")
    private String additionalInfo;

    private Boolean isPrimary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20) default 'ACTIVE'")
    private StatusEnum status = StatusEnum.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;
}

