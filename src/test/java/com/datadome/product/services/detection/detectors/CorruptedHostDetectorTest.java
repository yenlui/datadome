package com.datadome.product.services.detection.detectors;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.apache.Host;
import com.datadome.product.apache.Request;
import com.datadome.product.services.reporting.DetectionReport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CorruptedHostDetectorTest {
  @InjectMocks
  private CorruptedHostDetector detector;

  @Mock
  private DetectionReport detectionReportMock;

  @Test
  public void shouldNotBlockValid() {
    AccessLog accessLog = AccessLog
      .builder()
      .request(Request.builder().host(Host.of("127.0.0.1")).build())
      .build();

    Assertions.assertThat(detector.detect(accessLog)).isFalse();
  }

  @Test
  public void shouldBlockInvalid() {
    AccessLog accessLog = AccessLog
      .builder()
      .request(Request.builder().host(Host.of("127.0.0.1.dummy")).build())
      .build();

    Assertions.assertThat(detector.detect(accessLog)).isTrue();
  }
}
