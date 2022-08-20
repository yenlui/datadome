package com.datadome.product.services.parsing;

import static org.assertj.core.api.Assertions.assertThat;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.apache.Host;
import com.datadome.product.apache.Query;
import com.datadome.product.services.utils.AccessLogDateService;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
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

  @Test
  public void shouldParseRemaining() {
    List<String> accessLogLines = List.of(
      "92.101.35.224 - - [19/Dec/2020:14:29:21 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "93.35.225.23 - - [20/Dec/2020:07:49:39 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "85.125.194.182 - - [20/Dec/2020:11:43:19 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "110.78.171.129 - - [20/Dec/2020:17:16:30 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "101.224.168.181 - - [21/Dec/2020:08:37:35 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "40.121.91.147 - - [21/Dec/2020:11:29:14 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "180.250.12.10 - - [21/Dec/2020:16:55:32 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "112.86.255.228 - - [21/Dec/2020:20:45:18 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322) pcdn/unknow/router/9.4.7.10159/410000010000000000005FDADF5300226D6307DC/c\" \"-\"",
      "174.76.48.232 - - [22/Dec/2020:06:49:15 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "106.110.56.211 - - [22/Dec/2020:11:29:31 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322) pcdn/unknow/router/9.4.7.12216/410000010000000000005FB4ACC900226D63288E/c\" \"-\"",
      "45.115.112.214 - - [22/Dec/2020:14:46:05 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "110.242.135.51 - - [23/Dec/2020:01:23:41 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "131.108.234.100 - - [23/Dec/2020:06:54:19 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "181.119.112.188 - - [23/Dec/2020:18:17:18 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "143.202.227.77 - - [23/Dec/2020:23:09:12 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "36.83.26.177 - - [23/Dec/2020:23:51:15 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "101.109.192.252 - - [24/Dec/2020:10:38:50 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "1.0.238.210 - - [24/Dec/2020:22:03:52 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "101.109.69.157 - - [25/Dec/2020:06:22:44 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "176.56.107.226 - - [25/Dec/2020:21:26:07 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "1.4.161.123 - - [26/Dec/2020:08:01:58 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "128.127.164.156 - - [27/Dec/2020:02:34:53 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "190.184.144.170 - - [27/Dec/2020:09:58:13 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "103.87.168.249 - - [27/Dec/2020:13:17:16 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "106.86.54.82 - - [27/Dec/2020:17:18:28 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "189.81.219.201 - - [28/Dec/2020:03:48:49 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "105.247.28.74 - - [28/Dec/2020:12:11:19 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "112.80.126.104 - - [28/Dec/2020:18:43:49 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322) pcdn/unknow/router/9.4.7.12216/410000010000000000005FB729EFB0D59DE6EC15/c\" \"-\"",
      "88.198.140.4 - - [01/Jan/2021:20:44:04 +0100] \"GET /robots.txt HTTP/1.1\" 200 304 \"\" \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:41.0) Gecko/20100101 Firefox/41.0\" \"-\"",
      "190.0.242.217 - - [02/Jan/2021:07:59:55 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "85.90.105.201 - - [02/Jan/2021:10:29:28 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "212.64.65.172 - - [02/Jan/2021:13:18:28 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "46.55.25.191 - - [02/Jan/2021:22:22:26 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "131.108.118.103 - - [03/Jan/2021:00:33:04 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "62.201.233.59 - - [03/Jan/2021:07:28:51 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "196.3.171.138 - - [03/Jan/2021:14:17:15 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "170.244.0.179 - - [03/Jan/2021:16:09:31 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "113.117.134.222 - - [03/Jan/2021:19:43:47 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322) pcdn/unknow/and/9.4.1.12170/110000010000000000005FE829BDFFE5778A69FF/c\" \"-\"",
      "110.77.171.185 - - [03/Jan/2021:23:34:01 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "174.76.48.252 - - [04/Jan/2021:17:26:10 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "112.86.196.49 - - [04/Jan/2021:20:00:35 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322) pcdn/unknow/and/9.4.1.12170/110000010000000000005FF00B15FF836CF8F2FF/c\" \"-\"",
      "13.92.119.142 - - [04/Jan/2021:23:48:31 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "175.106.18.201 - - [05/Jan/2021:05:37:31 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "101.108.188.112 - - [05/Jan/2021:08:26:33 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "89.187.177.105 - - [05/Jan/2021:14:04:37 +0100] \"GET http://almhuette-raith.at/administrator/index.php HTTP/1.1\" 200 4267 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "196.216.95.100 - - [05/Jan/2021:15:08:07 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "47.242.156.55 - - [05/Jan/2021:19:36:59 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "95.31.119.210 - - [05/Jan/2021:22:07:52 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "91.223.32.102 - - [06/Jan/2021:06:57:09 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "110.229.156.30 - - [06/Jan/2021:09:31:13 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "47.242.170.47 - - [06/Jan/2021:13:22:31 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "101.51.141.17 - - [06/Jan/2021:15:22:59 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "92.101.200.136 - - [06/Jan/2021:20:38:38 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "92.101.200.136 - - [07/Jan/2021:07:50:55 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "92.101.200.136 - - [07/Jan/2021:09:18:04 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\"",
      "92.101.200.136 - - [08/Jan/2021:06:01:32 +0100] \"GET /administrator/index.php HTTP/1.1\" 200 4263 \"\" \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" \"-\""
    );

    for (String accessLogLine : accessLogLines) {
      AccessLog accessLog = service.parseAccessLog(accessLogLine);
      assertThat(accessLog).isNotNull();
    }
  }
}
