package com.datadome.product.services.parsing;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.apache.Host;
import com.datadome.product.apache.Request;
import com.datadome.product.apache.Response;
import com.datadome.product.services.utils.AccessLogDateService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ParseLogService {
  /**
   * Test regex here: https://regex101.com/
   * Regex : ^([\d.]+) (\S+) (\S+) \[([\w:/]+\s[+-]\d{4})\] \"(.+?)\" (\d+) (\d+) \"(.*?)\" \"(.+?)\"
   */
  private static String LOG_PARSER_REGEX =
    //"^(\\S+)";
    "^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+-]\\d{4})\\] \\\"(.+?)\\\" (\\d+) (-|\\d+) \\\"(.*?)\\\" \\\"(.+?)\\\" \\\"(.+?)\\\"";

  @Setter(onMethod = @__({ @Autowired }))
  private AccessLogDateService accessLogDateService;

  @Setter(onMethod = @__({ @Autowired }))
  private QueryService queryService;

  /**
   * Takes the access log line and build an ApacheLog object arround it.
   * @param accessLogLine
   * @return
   */
  public AccessLog parseAccessLog(String accessLogLine) {
    final Pattern pattern = Pattern.compile(
      LOG_PARSER_REGEX,
      Pattern.MULTILINE
    );

    if (StringUtils.isBlank(accessLogLine)) return null;

    try {
      Matcher matcher = pattern.matcher(accessLogLine);

      if (!matcher.matches()) {
        throw new IllegalArgumentException();
      }
      return this.buildApacheLog(matcher, accessLogLine);
    } catch (Exception exception) {
      log.error(String.format("Cannot parse: %s", accessLogLine), exception);
      return null;
    }
  }

  private AccessLog buildApacheLog(Matcher matcher, String accessLogLine) {
    return AccessLog
      .builder()
      .request(this.buildRequest(matcher))
      .response(this.buildResponse(matcher))
      .sourceLine(accessLogLine)
      .build();
  }

  private Request buildRequest(Matcher matcher) {
    return Request
      .builder()
      .host(Host.of(matcher.group(1)))
      .time(accessLogDateService.parse(matcher.group(4)))
      .query(queryService.parse(matcher.group(5)))
      .referer(matcher.group(8))
      .userAgent(matcher.group(9))
      .build();
  }

  private Response buildResponse(Matcher matcher) {
    long size = "-".equals(matcher.group(7))
      ? 0
      : Long.parseLong(matcher.group(7));

    return Response
      .builder()
      .statusCode(Integer.parseInt(matcher.group(6)))
      .size(size)
      .build();
  }
}
