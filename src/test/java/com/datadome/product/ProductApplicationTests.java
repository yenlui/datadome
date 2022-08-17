package com.datadome.product;

import com.datadome.product.services.reporting.DetectionReport;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.io.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ProductApplicationTests {
  @Setter(onMethod = @__({ @Autowired }))
  private EntryPoint entryPoint;

  @Setter(onMethod = @__({ @Autowired }))
  private DetectionReport detectionReport;

  private File targetRun;

  @BeforeEach
  public void cleanFolder() throws IOException {
    targetRun = new File("./target/run");
    FileUtils.deleteDirectory(targetRun);
  }

  @Test
  void sendRequests() throws IOException {
    // Random guy: 81.5.193.243

    URL testFileResource =
      this.getClass().getClassLoader().getResource("apache.log");
    Collection<String> lines = IOUtil.readLines(testFileResource.openStream());

    long passingCount = lines
      .stream()
      .filter(StringUtils::isNotBlank)
      .filter(entryPoint::handleAccessLog)
      .count();

    detectionReport.saveCsvReport(targetRun);

    log.info("Passing queries: {} / {}", passingCount, lines.size());
  }
}
