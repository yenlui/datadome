package com.datadome.product.services.detection.detectors.records;

import java.time.ZonedDateTime;

import lombok.Builder;

@Builder
public record CoolDown(ZonedDateTime lastSeen, long accumulation) {
    
}
