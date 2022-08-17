package com.datadome.product;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.services.detection.DetectionService;
import com.datadome.product.services.parsing.ParseLogService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntryPoint {
  @Setter(onMethod = @__({ @Autowired }))
  private ParseLogService parseService;

  @Setter(onMethod = @__({ @Autowired }))
  private DetectionService detectionService;

  /**
   *
   * @param accessLogLine
   * @return true if access should be granted
   */
  public boolean handleAccessLog(String accessLogLine) {
    AccessLog accessLog = parseService.parseAccessLog(accessLogLine);
    return accessLog == null
      ? true // fall back case, accessLogLine wasn't parsed properly
      : !detectionService.processAccessLog(accessLog);
  }
}
