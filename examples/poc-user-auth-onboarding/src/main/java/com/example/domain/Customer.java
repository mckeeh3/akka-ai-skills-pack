package com.example.domain;

import java.time.Instant;

public record Customer(
  String customerId,
  String tenantId,
  String name,
  boolean active,
  Instant createdAt,
  Instant updatedAt
) {
  public static Customer empty(String customerId) {
    var now = Instant.now();
    return new Customer(customerId, "", "", false, now, now);
  }

  public boolean exists() {
    return tenantId != null && !tenantId.isBlank() && name != null && !name.isBlank();
  }
}
