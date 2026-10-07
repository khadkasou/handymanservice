package com.nepbrain.technology.bucket.payload.request;

import lombok.*;

/*
 * @Created At 2/25/2024
 * @Author Suroj Awal
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BucketRequest {

    String encodedData;
    String folderPath;
    String fileName;
}
