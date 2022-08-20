package com.datadome.product.services.detection.detectors;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.apache.Host;
import com.datadome.product.services.detection.IDetector;
import com.datadome.product.services.reporting.DetectionReport;
import java.util.regex.Pattern;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

public class CorruptedHostDetector implements IDetector {
  private static Pattern VALID_HOST_PATTERN = Pattern.compile(
    "^\\d{1,3}(\\.\\d{1,3}){3}$"
  );

  @Setter(onMethod = @__({ @Autowired }))
  private DetectionReport detectionReport;

  @Override
  public boolean detect(AccessLog accessLog) {
    if (
      "145.219.89.34.bc.googleusercontent.com".equals(
          accessLog.request().host().value()
        )
    ) {
      System.out.println();
    }

    Host host = accessLog.request().host();
    if (!VALID_HOST_PATTERN.matcher(host.value()).matches()) {
      detectionReport.addDetection(host, "Invalid host");
      return true;
    }
    return false;
  }
}
