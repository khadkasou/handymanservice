package com.nepbrain.technology.bucket.util;

import com.nepbrain.technology.core.exception.GlobalException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility for determining content type from raw file bytes or Base64 payload.
 * Supports major file types including JPG, PNG, DOCX, XLSX, PDF, etc.
 */
@Service
@Slf4j
public class ContentTypeFromBase64 {

    @Value("classpath:content.properties")
    private Resource contentPropertiesFile;

    /** content-type → magic header bytes (used by {@link #identifyContentType}). */
    private static final Map<String, byte[]> MAGIC_HEADERS = new LinkedHashMap<>();

    private static final String XLSX_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String DOCX_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static final byte[] ZIP_MAGIC = new byte[]{0x50, 0x4B, 0x03, 0x04}; // Common for DOCX/XLSX

    @PostConstruct
    public void init() throws GlobalException {
        loadDefaultMagicHeaders();
        loadFromProperties(contentPropertiesFile);
        log.info("ContentTypeFromBase64 initialized with {} magic headers", MAGIC_HEADERS.size());
    }

    private void loadDefaultMagicHeaders() {
        MAGIC_HEADERS.put("image/jpeg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});
        MAGIC_HEADERS.put("image/png", new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47});
        MAGIC_HEADERS.put("application/msword", new byte[]{(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0}); // DOC
        // PDF: "%PDF-" — required for leave medical certificates and company documents
        MAGIC_HEADERS.put("application/pdf", "%PDF-".getBytes(StandardCharsets.US_ASCII));
        MAGIC_HEADERS.put("image/svg+xml", "<svg".getBytes(StandardCharsets.US_ASCII));
    }

    private void loadFromProperties(Resource resource) throws GlobalException {
        if (resource == null) {
            return;
        }
        try {
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            for (String key : props.stringPropertyNames()) {
                String type = props.getProperty(key);
                if (type == null || type.isBlank() || key == null || key.isBlank()) {
                    continue;
                }
                // Properties map magic-string → content-type; register for detection.
                MAGIC_HEADERS.putIfAbsent(type.trim(), key.getBytes(StandardCharsets.US_ASCII));
            }
        } catch (IOException e) {
            throw new GlobalException("BUK010", e);
        }
    }

    public static String identifyContentType(byte[] data) throws GlobalException {
        if (data == null || data.length == 0) {
            throw new GlobalException("BUK011", "Empty or invalid file data");
        }

        // DOCX / XLSX (ZIP-based) — check before generic PK from properties
        if (startsWith(data, ZIP_MAGIC)) {
            String openXmlType = detectOpenXmlFileType(data);
            if (openXmlType != null) {
                return openXmlType;
            }
        }

        for (Map.Entry<String, byte[]> entry : MAGIC_HEADERS.entrySet()) {
            if (startsWith(data, entry.getValue())) {
                return entry.getKey();
            }
        }

        throw new GlobalException("BUK012", "Unsupported or invalid file format");
    }

    private static String detectOpenXmlFileType(byte[] data) {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(data))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName().toLowerCase(Locale.ROOT);

                if (entryName.contains("..") || entryName.startsWith("/") || entryName.startsWith("\\")) {
                    throw new SecurityException("Potential Zip Slip vulnerability detected: " + entryName);
                }

                if (entryName.startsWith("xl/") && entryName.contains("workbook.xml")) {
                    return XLSX_TYPE;
                }
                if (entryName.startsWith("word/") && entryName.contains("document.xml")) {
                    return DOCX_TYPE;
                }
            }
        } catch (IOException e) {
            log.debug("Unable to detect OpenXML file type: {}", e.getMessage());
        }
        return null;
    }

    private static boolean startsWith(byte[] data, byte[] pattern) {
        if (data.length < pattern.length) {
            return false;
        }
        for (int i = 0; i < pattern.length; i++) {
            if (data[i] != pattern[i]) {
                return false;
            }
        }
        return true;
    }

    public static String getFileExtensionFromContentType(String contentType) {
        if (contentType == null) {
            return null;
        }
        return switch (contentType) {
            case "text/csv" -> "csv";
            case DOCX_TYPE -> "docx";
            case XLSX_TYPE -> "xlsx";
            case "application/pdf" -> "pdf";
            case "image/svg+xml" -> "svg";
            case "image/png" -> "png";
            case "image/jpeg" -> "jpg";
            case "application/msword" -> "doc";
            default -> null;
        };
    }
}
