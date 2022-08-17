package com.datadome.product.services.detection;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.services.reporting.DetectionReport;
import java.util.List;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class DetectionService {
  @Setter(onMethod = @__({ @Autowired }))
  private List<IDetector> detectors;

  @Setter(onMethod = @__({ @Autowired }))
  private DetectionReport detectionReport;

  /**
   * @param accessLog
   * @return true if any detector detects a threat
   */
  @Async
  public boolean processAccessLog(AccessLog accessLog) {
    if (accessLog == null) throw new IllegalArgumentException();
    return detectors.stream().anyMatch(detector -> detector.detect(accessLog));
  }
}
