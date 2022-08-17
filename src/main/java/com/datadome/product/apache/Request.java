package com.datadome.product.apache;

import java.time.ZonedDateTime;

import lombok.Builder;

@Builder
public record Request(Host host,ZonedDateTime time,Query query,String referer,String userAgent) {}
