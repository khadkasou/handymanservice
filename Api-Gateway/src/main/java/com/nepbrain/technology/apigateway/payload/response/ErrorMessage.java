package com.nepbrain.technology.apigateway.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessage {
  private String key;
  private String value;
}
