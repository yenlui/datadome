package com.datadome.product.services.detection;

import com.datadome.product.apache.AccessLog;

@FunctionalInterface
public interface IDetector {
  DetectionResult detect(AccessLog accessLog);
}
