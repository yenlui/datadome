package com.datadome.product.services.detection.detection;

import com.datadome.product.apache.Host;
import com.datadome.product.services.detection.DetectionResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleDetectionResult implements DetectionResult {
  public Host host;

  public String reason;
}
