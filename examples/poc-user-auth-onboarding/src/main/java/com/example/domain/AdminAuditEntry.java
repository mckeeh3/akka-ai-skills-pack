package com.example.domain;

import java.time.Instant;
import java.util.Map;

public record AdminAuditEntry(
  String auditId,
  String actorUserId,
  String effectiveUserId,
  String action,
  String targetType,
  String targetId,
  String tenantId,
  String customerId,
  Instant timestamp,
  String ipAddress,
  String userAgent,
  Map<String, String> metadata
) {}
