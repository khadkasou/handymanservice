package com.nepbrain.technology.apigateway.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.util.ObjectUtils;

public class JsonMaskerUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonMaskerUtil() {

    }

    public static String maskSensitiveFields(String json, String... sensitiveFields) throws IOException {
        if (ObjectUtils.isEmpty(json)) {
            return json;
        }

        JsonNode root;

        try {
            root = objectMapper.readTree(json);
        } catch (Exception e) {
            root = formUrlEncodedStringToJson(json);
        }

        for (String field : sensitiveFields) {
            maskField(root, field);
        }

        return objectMapper.writeValueAsString(root);
    }

    private static ObjectNode formUrlEncodedStringToJson(String formBody) {
        Map<String, String> map = new LinkedHashMap<>();
        String[] pairs = formBody.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx > 0) {
                String key = urlDecode(pair.substring(0, idx));
                String value = urlDecode(pair.substring(idx + 1));
                map.put(key, value);
            } else {
                String key = urlDecode(pair);
                map.put(key, "");
            }
        }
        ObjectNode jsonNode = objectMapper.createObjectNode();
        map.forEach(jsonNode::put);
        return jsonNode;
    }

    private static String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
    }

    private static void maskField(JsonNode node, String fieldName) {
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                if (entry.getKey().equals(fieldName)) {
                    ((ObjectNode) node).put(entry.getKey(), "*****");
                } else {
                    maskField(entry.getValue(), fieldName);
                }
            });
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                maskField(child, fieldName);
            }
        }
    }
}
