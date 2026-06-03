package ai.first.application.agentfoundation;

import akka.javasdk.agent.task.Task;
import java.time.Instant;
import java.util.List;

/** Akka AutonomousAgent task definitions for Audit/Trace summary reviews. */
public final class AuditTraceSummaryTasks {
  public static final Task<AuditTraceSummaryResult> SUMMARIZE_AUDIT_WINDOW = Task
      .name("AuditTraceSummary")
      .description("Summarize scoped and redacted audit/work trace evidence for authorized Audit/Trace human review")
      .resultConformsTo(AuditTraceSummaryResult.class)
      .rules(AuditTraceSummaryResultRule.class);

  private AuditTraceSummaryTasks() {}

  public static Task<AuditTraceSummaryResult> summarizeAuditWindowInstructions(AuditTraceSummaryRequest request) {
    return SUMMARIZE_AUDIT_WINDOW.instructions("""
        Run a governed Audit/Trace summary review for the selected tenant/customer window.

        Scope:
        - summaryTaskId: %s
        - tenantId: %s
        - customerId: %s
        - selectedAuthContextId: %s
        - requestedByAccountId: %s
        - windowStart: %s
        - windowEnd: %s
        - evidenceCategories: %s
        - correlationId: %s
        - governedCapability: %s
        - governedTool: audit.trace.summaryTask.start

        Governed runtime context:
        %s

        Redacted evidence/source refs available:
        %s

        Required output:
        - Return only the structured AuditTraceSummaryResult.
        - summaryTaskId, tenantId, customerId, windowStart, windowEnd, and correlationId must match the scope above.
        - Cite redacted evidenceRefs and traceRefs for every finding; hidden or unauthorized refs must be represented only as not_found_or_redacted.
        - Summarize provider readiness, authorization denial, agent work, prompt/skill/reference, tool invocation, attention, and workstream-event evidence where present.
        - The result is advisory and noDirectMutation must be true; do not claim to mutate traces, policies, users, memberships, roles, provider config, attention, or managed-agent behavior.
        - Omit raw JWTs, provider secrets, API keys, raw/hidden prompts, invitation tokens, raw tool payloads, and cross-tenant data.
        - If provider/model/runtime/tool/evidence config is unavailable, fail closed with an actionable reason instead of fabricating deterministic/model-less successful findings.
        """.formatted(
            request.summaryTaskId(),
            request.tenantId(),
            request.customerId() == null ? "" : request.customerId(),
            request.selectedAuthContextId(),
            request.requestedByAccountId(),
            request.windowStart(),
            request.windowEnd(),
            String.join(",", request.evidenceCategories()),
            request.correlationId(),
            request.capabilityId(),
            request.governedRuntimeContext(),
            String.join(", ", request.evidenceRefs())));
  }

  public record AuditTraceSummaryRequest(
      String summaryTaskId,
      String tenantId,
      String customerId,
      String selectedAuthContextId,
      String requestedByAccountId,
      Instant windowStart,
      Instant windowEnd,
      List<String> evidenceCategories,
      String correlationId,
      String capabilityId,
      String governedRuntimeContext,
      List<String> evidenceRefs) {
    public AuditTraceSummaryRequest {
      evidenceCategories = List.copyOf(evidenceCategories == null ? List.of() : evidenceCategories);
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    }
  }
}
