package com.nepbrain.technology.communication.payload.request;

import com.nepbrain.technology.communication.enums.MessageTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppMessageRequest {

    @NotBlank(message = "Locale cannot be empty or contain only whitespaces.")
    private String locale;
    @NotNull(message = "Message type cannot be null.")
    private MessageTypeEnum messageType;

}