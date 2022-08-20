package com.datadome.product;

import com.datadome.product.apache.Host;
import com.datadome.product.services.reporting.DetectionReport;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
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

  private List<Host> soundsLikeGoodGuys;

  private List<Host> soundsLikeBadGuys;

  @BeforeEach
  public void cleanFolder() throws IOException {
    targetRun = new File("./target/run");
    FileUtils.deleteDirectory(targetRun);

    soundsLikeGoodGuys =
      List.of(Host.of("81.5.193.243"), Host.of("91.114.166.95"));
    soundsLikeBadGuys =
      List.of(
        Host.of("193.106.31.130"),
        Host.of("145.219.89.34.bc.googleusercontent.com"),
        Host.of("5.176.255.173.unassigned.as54203.net")
      );
  }

  @Test
  void sendRequests() throws IOException {
    URL testFileResource =
      this.getClass().getClassLoader().getResource("apache.log");
    Collection<String> lines = IOUtil.readLines(testFileResource.openStream());

    long passingCount = lines
      .stream()
      .filter(StringUtils::isNotBlank)
      .filter(entryPoint::handleAccessLog)
      .count();

    for (Host goodGuy : soundsLikeGoodGuys) {
      Assertions
        .assertThat(detectionReport.containsDetectionsFor(goodGuy))
        .as("%s should be a good guy", goodGuy)
        .isFalse();
    }

    for (Host badGuy : soundsLikeBadGuys) {
      Assertions
        .assertThat(detectionReport.containsDetectionsFor(badGuy))
        .as("%s should be a bad guy", badGuy)
        .isTrue();
    }

    detectionReport.saveCsvReport(targetRun);

    log.info("Passing queries: {} / {}", passingCount, lines.size());
  }
}
