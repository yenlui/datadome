package com.datadome.product;

import com.datadome.product.services.reporting.DetectionReport;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.io.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
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
    URL testFileResource =
      this.getClass().getClassLoader().getResource("apache.log");
    Collection<String> lines = IOUtil.readLines(testFileResource.openStream());

    lines
      .stream()
      .filter(StringUtils::isNotBlank)
      .forEach(entryPoint::handleAccessLog);

    detectionReport.saveCsvReport(targetRun);
  }
}
