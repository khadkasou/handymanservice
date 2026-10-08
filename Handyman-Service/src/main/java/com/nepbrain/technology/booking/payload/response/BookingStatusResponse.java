package com.nepbrain.technology.booking.payload.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingStatusResponse {

    private UUID id;
    private String value;
    private String label;
    private String color;
    private Integer sortOrder;
}
