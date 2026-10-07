package com.nepbrain.technology.communication.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/*
 * @Created At 18/04/2024
 * @Author manohar.bhattarai
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CountryResponse {
    UUID id;
    String name;
    String nationality;
    String iso2;
    String iso3;
    String teleCode;
    String image;
    String status;
}
