package com.example.application;

import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.FunctionTool;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Read-only scoped evidence facade for Audit/Trace summary AutonomousAgent tasks. */
public class AuditTraceSummaryEvidenceTools {
  public static final String TOOL_ID = AuditTraceSummaryTasks.EVIDENCE_TOOL_ID;

  private final String tenantId;
  private final String customerId;
  private final List<EvidenceRecord> records;

  public AuditTraceSummaryEvidenceTools(String tenantId, String customerId, List<EvidenceRecord> records) {
    this.tenantId = Objects.requireNonNull(tenantId);
    this.customerId = customerId;
    this.records = List.copyOf(records == null ? List.of() : records);
  }

  @FunctionTool(description = "Read scoped, browser-safe Audit/Trace evidence. Read-only; no direct mutation, exports, authorization bypass, or raw secret/prompt/tool payload access.")
  public String read(@Description("Evidence request containing tenantId/customerId/traceId/filter hints") String evidenceRequest) {
    var requestedTenant = valueAfter(evidenceRequest, "tenantId=");
    if (requestedTenant != null && !requestedTenant.equals(tenantId)) {
      return "tool_id=" + TOOL_ID + "\ndecision=denied\nreason=not_found_or_redacted\nredaction=cross-tenant evidence omitted";
    }
    var requestedCustomer = valueAfter(evidenceRequest, "customerId=");
    if (requestedCustomer != null && customerId != null && !requestedCustomer.equals(customerId)) {
      return "tool_id=" + TOOL_ID + "\ndecision=denied\nreason=not_found_or_redacted\nredaction=cross-customer evidence omitted";
    }
    var visible = new ArrayList<String>();
    for (var record : records) {
      if (!tenantId.equals(record.tenantId())) continue;
      if (customerId != null && !Objects.equals(customerId, record.customerId())) continue;
      visible.add(record.traceId() + ":" + record.category() + ":" + redact(record.safeSummary()));
    }
    if (visible.isEmpty()) visible.add("not_found_or_redacted");
    return "tool_id=" + TOOL_ID
        + "\ndecision=allowed"
        + "\nmode=read_only_no_direct_mutation"
        + "\ntenantId=" + redact(tenantId)
        + "\ncustomerId=" + redact(customerId)
        + "\nevidence=" + String.join(" | ", visible)
        + "\nredaction=provider credentials, API keys, raw JWTs, invitation tokens, hidden prompts, raw tool payloads, support-only data, and cross-tenant/customer data omitted";
  }

  private static String valueAfter(String text, String marker) {
    if (text == null) return null;
    var index = text.indexOf(marker);
    if (index < 0) return null;
    var value = text.substring(index + marker.length()).split("[\\s,;]")[0].trim();
    return value.isBlank() ? null : value;
  }

  private static String redact(String value) {
    if (value == null) return "null";
    return value.replaceAll("(?i)(api[_-]?key|secret|token|credential|bearer|jwt)\\s*[:=]?\\s*\\S+", "$1=[REDACTED]");
  }

  public record EvidenceRecord(String tenantId, String customerId, String traceId, String category, String safeSummary) {}
}
