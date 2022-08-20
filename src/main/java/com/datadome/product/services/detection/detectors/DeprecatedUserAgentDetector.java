package com.datadome.product.services.detection.detectors;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.services.detection.IDetector;
import com.datadome.product.services.reporting.DetectionReport;
import java.util.List;
import java.util.regex.Pattern;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeprecatedUserAgentDetector implements IDetector {
  private List<Pattern> deprecatedPatterns;

  @Setter(onMethod = @__({ @Autowired }))
  private DetectionReport detectionReport;

  public DeprecatedUserAgentDetector() {
    this.deprecatedPatterns = List.of(Pattern.compile("compatible.+MSIE"));
  }

  @Override
  public boolean detect(AccessLog accessLog) {
    String userAgent = accessLog.request().userAgent();

    if (
      deprecatedPatterns
        .stream()
        .anyMatch(
          pattern -> {
            return pattern.matcher(userAgent).matches();
          }
        )
    ) {
      detectionReport.addDetection(
        accessLog.request().host(),
        String.format("Deprecated User Agent: %s", userAgent)
      );
      return true;
    }

    return false;
  }
}
