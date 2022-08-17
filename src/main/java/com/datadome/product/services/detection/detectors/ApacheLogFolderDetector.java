package com.datadome.product.services.detection.detectors;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.services.detection.IDetector;
import com.datadome.product.services.reporting.DetectionReport;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApacheLogFolderDetector implements IDetector {
  @Setter(onMethod = @__({ @Autowired }))
  private DetectionReport detectionReport;

  @Override
  public boolean detect(AccessLog accessLog) {
    if (
      accessLog
        .request()
        .query()
        .uri()
        .getPath()
        .contains("/apache-log/access.log")
    ) {
      detectionReport.addDetection(
        accessLog.request().host(),
        "Try to access restricted folder"
      );
      return true;
    }

    return false;
  }
}
