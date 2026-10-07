package com.nepbrain.technology.communication.payload.request;

/*
 * @Created At 17/03/2024
 * @Author suroj.awal
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmsRequest {
    @JsonProperty(value = "isClientLogin")
    private String isClientLogin;
    private Date date;
    private String batchId;
    @JsonProperty(value = "ReceiverNo")
    private String receiverNo;
    @JsonProperty(value = "Message")
    private String message;
    @JsonProperty(value = "Username")
    private String username;
    @JsonProperty(value = "Password")
    private String password;
    @JsonProperty(value = "OrganisationCode")
    private String organisationCode;
    private List<BulkSMSRequest> smsDetails;

}
