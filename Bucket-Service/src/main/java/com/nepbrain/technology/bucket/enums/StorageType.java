package com.nepbrain.technology.bucket.enums;

import lombok.Getter;

@Getter
public enum StorageType {
    MINIO("MINIO"),
    AWS_S3("AWS_S3");

    private final String value;

    StorageType(String value) {
        this.value = value;
    }

    public static StorageType fromString(String value) {
        if (value == null || value.isEmpty()) {
            return MINIO; // Default to MinIO
        }
        
        for (StorageType type : StorageType.values()) {
            if (type.value.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        
        // Default to MinIO if invalid value
        return MINIO;
    }
}

