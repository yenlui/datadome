package com.datadome.product.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.datadome.product.ProductApplication;
import com.datadome.product.apache.AccessLog;
import com.datadome.product.apache.Host;
import com.datadome.product.apache.Query;
import com.datadome.product.services.detection.DetectionService;
import com.datadome.product.services.routing.ForwardService;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

@SpringBootTest(
  classes = ProductApplication.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class MainControllerTest {
  private static final ZoneId UTC = ZoneId.of("UTC");

  @MockBean
  private DetectionService detectionServiceMock;

  @MockBean
  private ForwardService forwardServiceMock;

  @LocalServerPort
  private int port;

  TestRestTemplate restTemplate = new TestRestTemplate();

  @Captor
  private ArgumentCaptor<AccessLog> accessLogCaptor;

  @Test
  public void ensureControllerCapacityToHandleRequests() {
    when(detectionServiceMock.processAccessLog(accessLogCaptor.capture()))
      .thenReturn(true);

    MultiValueMap<String, String> headers = new HttpHeaders();
    headers.put("date", List.of("19/Dec/2020:13:57:26 +0100"));
    HttpEntity<?> request = new HttpEntity<>(headers);

    ResponseEntity<String> response = restTemplate.exchange(
      "http://localhost:" + port + "/path/to/resource",
      HttpMethod.GET,
      request,
      String.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    verify(forwardServiceMock).forwardRequest();

    AccessLog accessLog = accessLogCaptor.getValue();
    assertThat(accessLog).isNotNull();
    assertThat(accessLog.request()).isNotNull();
    assertThat(accessLog.request().host()).isEqualTo(Host.of("127.0.0.1"));
    assertThat(accessLog.request().query())
      .isEqualTo(
        Query
          .builder()
          .method(HttpMethod.GET)
          .protocol("HTTP/1.1")
          .uri("/path/to/resource")
          .build()
      );
    assertThat(accessLog.request().referer()).isNull();
    assertThat(accessLog.request().time().withZoneSameInstant(UTC))
      .isEqualTo(ZonedDateTime.of(2020, 12, 19, 12, 57, 26, 0, UTC));
    assertThat(accessLog.request().userAgent()).isNull();
    // assertThat(accessLog.response()).isNotNull();
    // assertThat(accessLog.response().size()).isEqualTo(217);
    // assertThat(accessLog.response().statusCode()).isEqualTo(404);
  }
}
