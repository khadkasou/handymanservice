package com.nepbrain.technology.communication.payload.response;

/*
 * @Created At 10/03/2024
 * @Author suroj.awal
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserList {

    UUID id;
    String name;

}
