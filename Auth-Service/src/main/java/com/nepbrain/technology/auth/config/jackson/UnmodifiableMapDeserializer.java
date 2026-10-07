package com.nepbrain.technology.auth.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class UnmodifiableMapDeserializer extends JsonDeserializer<Map<?, ?>> {

  @Override
  public Map<?, ?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    ObjectMapper mapper = (ObjectMapper) parser.getCodec();
    JsonNode mapNode = mapper.readTree(parser);
    Map<String, Object> result = new LinkedHashMap<>();
    if (mapNode != null && mapNode.isObject()) {
      java.util.Iterator<String> fieldNames = mapNode.fieldNames();
      while (fieldNames.hasNext()) {
        String fieldName = fieldNames.next();
        JsonNode value = mapNode.get(fieldName);
        result.put(fieldName, mapper.readValue(value.traverse(mapper), Object.class));
      }
    }
    return Collections.unmodifiableMap(result);
  }
}

