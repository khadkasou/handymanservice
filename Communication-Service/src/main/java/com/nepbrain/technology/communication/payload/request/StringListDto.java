package com.nepbrain.technology.communication.payload.request;

import lombok.*;

import java.util.List;

/**
 * @author manohar.bhattarai on 4/29/2024
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StringListDto {
    private List<String> list;
}
