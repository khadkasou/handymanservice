package com.nepbrain.technology.communication.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * @author manohar.bhattarai on 4/15/2024
 */
@Getter
@Setter
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseWithIdAndValue {

    private UUID id;
    private String value;
}
