package com.nepbrain.technology.bucket.payload.request;

import com.nepbrain.technology.bucket.enums.UploadType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/*
 * @Created At 15/04/2024
 * @Author Suroj Awal
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentRequest {
    @NotNull(message = "Image is missing")
    @NotEmpty(message = "Image is missing")
    private String encodedData;
    private UploadType type;
    @NotNull(message = "Image module is missing")
    @NotEmpty(message = "Image module is missing")
    private String module;
    @NotNull(message = "Image name is missing")
    @NotEmpty(message = "Image name is missing")
    private String fileName;
}
