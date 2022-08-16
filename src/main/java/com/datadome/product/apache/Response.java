package com.datadome.product.apache;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {
  private int statusCode;

  private long size;
}
