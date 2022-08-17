package com.datadome.product.services.detection.detectors.records;

import com.datadome.product.apache.Host;

import lombok.Builder;

@Builder
public record CoolDownKey(Host host, String query) {
    
}
