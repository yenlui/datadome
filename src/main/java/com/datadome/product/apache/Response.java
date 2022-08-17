package com.datadome.product.apache;

import lombok.Builder;

@Builder
public record Response(int statusCode,long size) {
}
