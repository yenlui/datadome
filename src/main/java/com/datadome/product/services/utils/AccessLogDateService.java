package com.datadome.product.services.utils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class AccessLogDateService {
  private static final DateTimeFormatter formatter = DateTimeFormatter
    .ofPattern("dd/MMM/yyyy:HH:mm:ss Z")
    .withLocale(Locale.US);

  /**
   * Parse a date with the appropriate format
   * @param dateTimeString
   * @return
   */
  public ZonedDateTime parse(String dateTimeString) {
    return ZonedDateTime.parse(dateTimeString, formatter);
  }
}
