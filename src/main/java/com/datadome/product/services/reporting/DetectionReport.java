package com.datadome.product.services.reporting;

import com.datadome.product.apache.Host;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DetectionReport {
  private static final String SEPARATOR = ",";
  private Map<Host, Map<String, Long>> detections = new HashMap<>();

  public void addDetection(Host host, String reason) {
    // Host host = detectionResult.getHost();
    // String reason = detectionResult.getReason();

    Map<String, Long> reasons = detections.getOrDefault(host, new HashMap<>());
    detections.putIfAbsent(host, reasons);

    Long hitCount = reasons.getOrDefault(reason, 0L);
    reasons.put(reason, hitCount + 1);
  }

  public void saveCsvReport(File targetFolder) {
    if (!targetFolder.exists()) targetFolder.mkdirs();

    File reportFile = new File(
      targetFolder,
      String.format(
        "%s.csv",
        ZonedDateTime
          .now()
          .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh-mm-ss"))
      )
    );

    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer
      .append(String.join(SEPARATOR, "host", "reason", "hits"))
      .append(System.lineSeparator());

    this.detections.forEach(
        (host, reasonEntry) ->
          reasonEntry.forEach(
            (reason, hits) ->
              stringBuffer
                .append(buildLine(host, reason, hits))
                .append(System.lineSeparator())
          )
      );

    try (
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
        new FileOutputStream(reportFile)
      )
    ) {
      outputStreamWriter.write(stringBuffer.toString());
      log.info("Report output to: {}", reportFile.getAbsolutePath());
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  private String buildLine(Host host, String reason, Long hits) {
    return String.join(SEPARATOR, host.toString(), reason, Long.toString(hits));
  }
}
