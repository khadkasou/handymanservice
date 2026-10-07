package com.nepbrain.technology.bucket.payload.response;

import com.nepbrain.technology.bucket.enums.MessageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {

    private String code;
    private MessageTypeEnum messageType;
    private String defaultEnglishMessage;
    private String languageSpecificMessage;
}