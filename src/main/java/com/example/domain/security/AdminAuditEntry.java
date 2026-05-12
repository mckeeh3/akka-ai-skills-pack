package com.example.domain.security;

import java.time.Instant;
import java.util.Map;

/** Immutable append-only admin/security audit fact. Store each entry under a unique audit id. */
public record AdminAuditEntry(
    String auditId,
    AdminAuditAction action,
    String actorUserId,
    String targetUserId,
    String tenantId,
    String customerId,
    Instant occurredAt,
    Map<String, String> details) {

  public enum AdminAuditAction {
    BOOTSTRAP_ADMIN,
    INVITE_USER,
    LINK_ACCOUNT,
    ACTIVATE_USER,
    DISABLE_USER,
    REACTIVATE_USER,
    REPLACE_ROLES,
    UPSERT_TENANT,
    UPSERT_CUSTOMER
  }
}
