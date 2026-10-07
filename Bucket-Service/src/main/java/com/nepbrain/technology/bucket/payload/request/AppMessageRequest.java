package com.nepbrain.technology.bucket.payload.request;

import com.nepbrain.technology.bucket.enums.MessageTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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