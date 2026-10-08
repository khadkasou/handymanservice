package com.nepbrain.technology.core.util;

import com.nepbrain.technology.core.exception.GlobalException;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.ObjectUtils;

public class JsonConverter {

    private JsonConverter() {}

    public static String textToJson(String text) {
        JSONObject headersJson = new JSONObject();

        if (!ObjectUtils.isEmpty(text)) {
            StringBuilder currentPart = new StringBuilder();
            boolean inQuotes = false;
            int length = text.length();

            for (int i = 0; i < length; i++) {
                char currentChar = text.charAt(i);

                // Toggle inQuotes flag when encountering a quote
                if (currentChar == '"') {
                    inQuotes = !inQuotes;
                }

                // If it's a comma and we're not inside quotes, process the current part
                if (currentChar == ',' && !inQuotes) {
                    processHeaderPart(currentPart.toString(), headersJson);
                    currentPart.setLength(0); // Reset the buffer for the next part
                } else {
                    // Otherwise, keep appending characters to the current part
                    currentPart.append(currentChar);
                }
            }

            // Process the last part after the last comma
            if (!currentPart.isEmpty()) {
                processHeaderPart(currentPart.toString(), headersJson);
            }
        }

        return headersJson.toString();
    }

    private static void processHeaderPart(String headerPart, JSONObject headersJson) {
        String[] keyValue = headerPart.split(":", 2);

        if (keyValue.length == 2) {
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");
            try {
                headersJson.put(key, value);
            } catch (JSONException e) {
                throw new GlobalException("ERR001", e);
            }
        }
    }
}
