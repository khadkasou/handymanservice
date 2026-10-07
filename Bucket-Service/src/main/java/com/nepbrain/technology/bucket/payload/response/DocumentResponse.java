package com.nepbrain.technology.bucket.payload.response;

import com.nepbrain.technology.bucket.enums.UploadType;
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
public class DocumentResponse {

    private String filePath;
    private UploadType type;
    private String fileName;

}
