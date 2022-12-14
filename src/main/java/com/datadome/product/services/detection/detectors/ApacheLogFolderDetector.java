package com.datadome.product.services.detection.detectors;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.services.detection.IDetector;
import com.datadome.product.services.reporting.DetectionReport;
import com.datadome.product.services.utils.UriHelper;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApacheLogFolderDetector implements IDetector, UriHelper {
  @Setter(onMethod = @__({ @Autowired }))
  private DetectionReport detectionReport;

  @Override
  public boolean detect(AccessLog accessLog) {
    try {
      URI uri = accessLogUri(accessLog);

      String path = uri.getPath();

      if (path.contains("/apache-log/access.log")) {
        detectionReport.addDetection(
          accessLog.request().host(),
          String.format("Try to access restricted folder: %s", path)
        );
        return true;
      }

      return false;
    } catch (URISyntaxException e) {
      return false;
    }
  }
}
