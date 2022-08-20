package com.datadome.product.services.utils;

import com.datadome.product.apache.AccessLog;
import java.net.URI;
import java.net.URISyntaxException;

public interface UriHelper {
  default URI accessLogUri(AccessLog accessLog) throws URISyntaxException {
    try {
      return URI.create(accessLog.request().query().uri());
    } catch (Exception exception) {
      throw (URISyntaxException) exception.getCause();
    }
  }
}
