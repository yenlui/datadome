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
      accessLog
        .getRequest()
        .getQuery()
        .getQuery()
        .contains("/apache-log/access.log")
    ) {
      return SimpleDetectionResult
        .builder()
        .host(accessLog.getRequest().getHost())
        .reason("Should not try to access this folder")
        .build();
    }

    return null;
  }
}
