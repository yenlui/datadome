package com.datadome.product.services.parsing;

import com.datadome.product.apache.Query;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QueryService {
  private static final String QUERY_PATTERN =
    "^(GET|POST|PUT|DELETE|PATCH|OPTION|HEAD) (.+?) (.+?)$";

  public Query parse(String queryString) {
    final Pattern pattern = Pattern.compile(QUERY_PATTERN, Pattern.MULTILINE);

    Matcher matcher = pattern.matcher(queryString);

    if (!matcher.matches()) log.debug(matcher.toString());

    return Query
      .builder()
      .method(HttpMethod.valueOf(matcher.group(1)))
      .uri(URI.create(matcher.group(2)))
      .protocol(matcher.group(3))
      .build();
  }
}
