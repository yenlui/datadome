package com.datadome.product;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.services.detection.DetectionService;
import com.datadome.product.services.parsing.ParseLogService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EntryPoint {
  @Setter(onMethod = @__({ @Autowired }))
  private ParseLogService parseService;

  @Setter(onMethod = @__({ @Autowired }))
  private DetectionService detectionService;

  @Async
  public void handleAccessLog(String accessLogLine) {
    AccessLog accessLog = parseService.parseAccessLog(accessLogLine);
    detectionService.processAccessLog(accessLog);
  }
}
