package com.datadome.product.services.detection;

import com.datadome.product.apache.Host;

public interface DetectionResult {
  Host getHost();

  String getReason();
}
