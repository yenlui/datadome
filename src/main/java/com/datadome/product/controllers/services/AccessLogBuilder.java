package com.datadome.product.controllers.services;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.apache.Host;
import com.datadome.product.apache.Query;
import com.datadome.product.apache.Request;
import com.datadome.product.services.utils.AccessLogDateService;
import javax.servlet.http.HttpServletRequest;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
public class AccessLogBuilder {
  @Setter(onMethod = @__({ @Autowired }))
  private AccessLogDateService accessLogDateService;

  public AccessLog fromRequest(
    String dateTimeString,
    HttpServletRequest request
  ) {
    return AccessLog
      .builder()
      .request(buildRequest(dateTimeString, request))
      .build();
  }

  private Request buildRequest(
    String dateTimeString,
    HttpServletRequest request
  ) {
    return Request
      .builder()
      .host(Host.of(request.getRemoteHost()))
      .query(buildQuery(request))
      .referer(request.getHeader("referer"))
      .time(accessLogDateService.parse(dateTimeString))
      .build();
  }

  private Query buildQuery(HttpServletRequest request) {
    return Query
      .builder()
      .method(HttpMethod.valueOf(request.getMethod()))
      .protocol(request.getProtocol())
      .uri(request.getRequestURI())
      .build();
  }
}
