package com.datadome.product.services.detection;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.services.reporting.DetectionReport;
import java.util.List;
import java.util.Objects;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetectionService {
  @Setter(onMethod = @__({ @Autowired }))
  private List<IDetector> detectors;

  @Setter(onMethod = @__({ @Autowired }))
  private DetectionReport detectionReport;

  public void processAccessLog(AccessLog accessLog) {
    if (accessLog == null) return;

    detectors
      .stream()
      .map(detector -> detector.detect(accessLog))
      .filter(Objects::nonNull)
      .forEach(detectionReport::addDetection);
  }
}
