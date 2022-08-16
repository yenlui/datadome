package com.datadome.product.apache;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
public class Request {
  private Host host;

  private ZonedDateTime time;

  private Query query;

  private String referer;

  private String userAgent;

  public void setReferer(String referer) {
    if (StringUtils.isBlank(referer)) return;
    if ("-".equals(referer.trim())) return;

    this.referer = referer;
  }
}
