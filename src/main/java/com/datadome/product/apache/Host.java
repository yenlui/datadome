package com.datadome.product.apache;

public record Host(String value) {
  public static Host of(String host) {
    return new Host(host);
  }
}
