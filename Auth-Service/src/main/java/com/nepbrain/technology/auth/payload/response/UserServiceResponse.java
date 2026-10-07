package com.nepbrain.technology.auth.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserServiceResponse {
    private UUID id;
    private String emailAddress;
    private String mobileNumber;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private Boolean enabled;
    private String status;
    private UUID roleId;
    private String roleName;
    private List<UUID> roleIds;
}

