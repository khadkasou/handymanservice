package com.nepbrain.technology.auth.config.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
abstract class StringArrayMixin {

  @JsonCreator
  StringArrayMixin(String[] array) {}
}

