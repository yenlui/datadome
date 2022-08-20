package com.datadome.product.services.parsing;

import static org.assertj.core.api.Assertions.assertThat;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.apache.Host;
import com.datadome.product.apache.Query;
import com.datadome.product.services.utils.AccessLogDateService;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;

@ExtendWith(MockitoExtension.class)
public class ParseLogServiceTest {
  private static final ZoneId UTC = ZoneId.of("UTC");

  @InjectMocks
  private ParseLogService service;

  @BeforeEach
  public void addDeps() {
    service.setAccessLogDateService(new AccessLogDateService());
    service.setQueryService(new QueryService());
  }

  @Test
  public void shouldParseNominal() {
    AccessLog accessLog = service.parseAccessLog(
      "157.48.153.185 - - [19/Dec/2020:14:08:08 +0100] \"GET /favicon.ico HTTP/1.1\" 404 217 \"http://www.almhuette-raith.at/apache-log/access.log\" \"Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36\" \"-\""
    );

    assertThat(accessLog).isNotNull();

    assertThat(accessLog.request()).isNotNull();
    assertThat(accessLog.request().host()).isEqualTo(Host.of("157.48.153.185"));
    assertThat(accessLog.request().query())
      .isEqualTo(
        Query
          .builder()
          .method(HttpMethod.GET)
          .protocol("HTTP/1.1")
          .uri("/favicon.ico")
          .build()
      );
    assertThat(accessLog.request().referer())
      .isEqualTo("http://www.almhuette-raith.at/apache-log/access.log");
    assertThat(accessLog.request().time().withZoneSameInstant(UTC))
      .isEqualTo(ZonedDateTime.of(2020, 12, 19, 13, 8, 8, 0, UTC));
    assertThat(accessLog.request().userAgent())
      .isEqualTo(
        "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36"
      );

    assertThat(accessLog.response()).isNotNull();
    assertThat(accessLog.response().size()).isEqualTo(217);
    assertThat(accessLog.response().statusCode()).isEqualTo(404);
  }

  @Test
  public void shouldParseStrangeHost() {
    AccessLog accessLog = service.parseAccessLog(
      "145.219.89.34.bc.googleusercontent.com - - [06/Jan/2021:14:28:26 +0100] \"GET /server-status HTTP/1.1\" 403 223 \"-\" \"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36 Lynt.cz\" \"-\""
    );

    assertThat(accessLog.request().host())
      .isEqualTo(Host.of("145.219.89.34.bc.googleusercontent.com"));
  }
}
