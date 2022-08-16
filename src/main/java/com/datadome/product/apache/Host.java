package com.datadome.product.apache;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(of = "value")
public class Host {
  @Getter
  private String value;

  private Host(String value) {
    this.value = value;
  }

  public static Host of(String host) {
    return new Host(host);
  }

  @Override
  public String toString() {
    return value;
  }
}
