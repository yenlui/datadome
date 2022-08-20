package com.datadome.product.services.detection.detectors;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.services.detection.IDetector;
import com.datadome.product.services.reporting.DetectionReport;
import com.datadome.product.services.utils.UriHelper;
import java.net.URISyntaxException;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

public class CorruptedUriDetector implements IDetector, UriHelper {
  @Setter(onMethod = @__({ @Autowired }))
  private DetectionReport detectionReport;

  @Override
  public boolean detect(AccessLog accessLog) {
    try {
      accessLogUri(accessLog);
      return false;
    } catch (URISyntaxException exception) {
      detectionReport.addDetection(
        accessLog.request().host(),
        exception.getMessage()
      );
      return true;
    }
  }
}
