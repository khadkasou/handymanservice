package com.nepbrain.technology.bucket.payload.request;

/*
 * @Created At 05/02/2025
 * @Author suroj.awal
 */

import com.nepbrain.technology.bucket.enums.UploadType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocRequest {
    private String encodedData;
    private UploadType type;
    private String module;
    private String fileName;
    private String contentType;
    private String extension;
    private byte[] decodedBytes;
}
