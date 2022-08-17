package com.datadome.product.services.detection;

import com.datadome.product.apache.AccessLog;

@FunctionalInterface
public interface IDetector {
  /**
   * Apply a specific rule in order to detect potential threat
   * @param accessLog
   * @return true if detection is blocking
   */
  boolean detect(AccessLog accessLog);
}
