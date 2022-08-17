package com.datadome.product.services.detection.detectors;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.apache.Host;
import com.datadome.product.services.detection.DetectionResult;
import com.datadome.product.services.detection.IDetector;
import com.datadome.product.services.detection.detection.SimpleDetectionResult;
import com.datadome.product.services.detection.detectors.records.CoolDown;
import com.datadome.product.services.detection.detectors.records.CoolDown.CoolDownBuilder;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import org.springframework.stereotype.Service;

@Service
public class CoolDownDetector implements IDetector {
  private static final int COOL_DOWN_VALUE = 100;
  private static final int COOL_DOWN_TRIGGER = 3000;

  private HashMap<Host, CoolDown> watched = new HashMap<>();

  @Override
  public DetectionResult detect(AccessLog accessLog) {
    Host host = accessLog.request().host();
    CoolDown previousCoolDown = watched.get(host);
    CoolDownBuilder coolDownBuilder = CoolDown
      .builder()
      .lastSeen(accessLog.request().time());

    if (previousCoolDown == null) {
      watched.putIfAbsent(host, coolDownBuilder.accumulation(0).build());
      return null;
    }

    long elapsedTime = computeElapsedTime(accessLog, previousCoolDown);

    CoolDown currentCooldown = coolDownBuilder
      .accumulation(
        computeAccumulation(previousCoolDown.accumulation(), elapsedTime)
      )
      .build();

    watched.put(host, currentCooldown);

    if (currentCooldown.accumulation() < COOL_DOWN_TRIGGER) {
      return null;
    }

    return SimpleDetectionResult
      .builder()
      .host(host)
      .reason("Does not comply cool down policy")
      .build();
  }

  private long computeAccumulation(
    long previousAccumulation,
    long elapsedTime
  ) {
    return Math.max(previousAccumulation + (COOL_DOWN_VALUE - elapsedTime), 0);
  }

  private long computeElapsedTime(
    AccessLog accessLog,
    CoolDown previousCoolDown
  ) {
    return ChronoUnit.MILLIS.between(
      accessLog.request().time(),
      previousCoolDown.lastSeen()
    );
  }
}
