package com.datadome.product.services.detection.detectors;

import com.datadome.product.apache.AccessLog;
import com.datadome.product.apache.Host;
import com.datadome.product.services.detection.IDetector;
import com.datadome.product.services.detection.detectors.records.CoolDown;
import com.datadome.product.services.detection.detectors.records.CoolDown.CoolDownBuilder;
import com.datadome.product.services.detection.detectors.records.CoolDownKey;
import com.datadome.product.services.reporting.DetectionReport;
import com.datadome.product.services.utils.UriHelper;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoolDownDetector implements IDetector, UriHelper {
  private static final int COOL_DOWN_VALUE = 1000;
  private static final int COOL_DOWN_WITHDRAW = -3 * COOL_DOWN_VALUE;
  private static final int COOL_DOWN_TRIGGER = 10 * COOL_DOWN_VALUE;

  @Setter(onMethod = @__({ @Autowired }))
  private DetectionReport detectionReport;

  private HashMap<CoolDownKey, CoolDown> watched = new HashMap<>();

  @Override
  public boolean detect(AccessLog accessLog) {
    Host host = accessLog.request().host();

    try {
      URI uri = accessLogUri(accessLog);
      CoolDownKey key = CoolDownKey
        .builder()
        .host(host)
        .query(uri.getPath())
        .build();

      CoolDown previousCoolDown = watched.get(key);

      CoolDownBuilder coolDownBuilder = CoolDown
        .builder()
        .lastSeen(accessLog.request().time());

      if (previousCoolDown == null) {
        watched.putIfAbsent(key, coolDownBuilder.accumulation(0).build());
        return false;
      }

      long elapsedTime = computeElapsedTime(accessLog, previousCoolDown);

      CoolDown currentCooldown = coolDownBuilder
        .accumulation(
          computeAccumulation(previousCoolDown.accumulation(), elapsedTime)
        )
        .build();

      watched.put(key, currentCooldown);

      if (currentCooldown.accumulation() < COOL_DOWN_TRIGGER) {
        return false;
      }

      detectionReport.addDetection(
        host,
        String.format("Does not comply cool down policy on: %s", key.query())
      );

      return true;
    } catch (URISyntaxException e) {
      return false;
    }
  }

  private long computeAccumulation(
    long previousAccumulation,
    long elapsedTime
  ) {
    long valueToAccumulate = Math.max(
      COOL_DOWN_VALUE - elapsedTime,
      COOL_DOWN_WITHDRAW
    );
    return Math.max(previousAccumulation + valueToAccumulate, 0);
  }

  private long computeElapsedTime(
    AccessLog accessLog,
    CoolDown previousCoolDown
  ) {
    return ChronoUnit.MILLIS.between(
      previousCoolDown.lastSeen(),
      accessLog.request().time()
    );
  }
}
