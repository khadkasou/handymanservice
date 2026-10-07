package com.nepbrain.technology.communication.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MasterCategoryItemResponse {

    UUID id;
    String name;
    String keyword;

}
