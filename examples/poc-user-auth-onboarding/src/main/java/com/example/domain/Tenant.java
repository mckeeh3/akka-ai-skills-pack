package com.example.domain;

import java.time.Instant;

public record Tenant(
  String tenantId,
  String name,
  boolean active,
  Instant createdAt,
  Instant updatedAt
) {
  public static Tenant empty(String tenantId) {
    var now = Instant.now();
    return new Tenant(tenantId, "", false, now, now);
  }

  public boolean exists() {
    return name != null && !name.isBlank();
  }
}
