package com.datadome.product.services.detection.detectors;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.services.detection.DetectionResult;
import com.datadome.product.services.detection.IDetector;
import com.datadome.product.services.detection.detection.SimpleDetectionResult;
import org.springframework.stereotype.Service;

@Service
public class ApacheLogFolderDetector implements IDetector {

  @Override
  public DetectionResult detect(AccessLog accessLog) {
    if (
      accessLog.request().query().query().contains("/apache-log/access.log")
    ) {
      return SimpleDetectionResult
        .builder()
        .host(accessLog.request().host())
        .reason("Try to access restricted folder")
        .build();
    }

    return null;
  }
}
