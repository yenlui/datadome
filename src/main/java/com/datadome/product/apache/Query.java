package com.datadome.product.apache;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpMethod;

@Data
@Builder
public class Query {
  private HttpMethod method;

  private String query;

  private String protocol;
}
