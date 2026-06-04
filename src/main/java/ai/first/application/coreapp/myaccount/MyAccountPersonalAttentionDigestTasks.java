package ai.first.application.coreapp.myaccount;

import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AuthContext;
import akka.javasdk.agent.task.Task;
import java.util.List;

/** Akka AutonomousAgent task definitions for My Account personal attention digests. */
public final class MyAccountPersonalAttentionDigestTasks {
  public static final Task<MyAccountPersonalAttentionDigestResult> PERSONAL_ATTENTION_DIGEST = Task
      .name("MyAccountPersonalAttentionDigest")
      .description("Summarize authorized personal attention for the signed-in account and selected AuthContext")
      .resultConformsTo(MyAccountPersonalAttentionDigestResult.class)
      .rules(MyAccountPersonalAttentionDigestResultRule.class);

  private MyAccountPersonalAttentionDigestTasks() {}

  public static Task<MyAccountPersonalAttentionDigestResult> personalAttentionDigestInstructions(PersonalAttentionDigestRequest request) {
    return PERSONAL_ATTENTION_DIGEST.instructions("""
        Run a governed My Account personal attention digest for the selected signed-in account.

        Scope:
        - digestTaskId: %s
        - tenantId: %s
        - customerId: %s
        - accountId: %s
        - selectedContextId: %s
        - idempotencyKey: %s
        - correlationId: %s
        - governedCapability: my_account.personal_attention_digest.start
        - governedTool: my_account.personalAttentionDigest.start

        Governed runtime context:
        %s

        Authorized personal attention evidence items supplied by the backend:
        %s

        Authorized evidence refs:
        %s

        Visible capability ids for navigation recommendations:
        %s

        Required output:
        - Return only the structured MyAccountPersonalAttentionDigestResult.
        - digestTaskId, tenantId, customerId, accountId, and selectedContextId must match the scope above.
        - authorizedAttentionCount must count only the authorized evidence items supplied in this task, never tenant/customer totals.
        - Cite only supplied evidenceRefs and traceRefs; hidden or unauthorized source refs must not be mentioned, counted, compared, inferred, or named.
        - Recommendations are advisory navigation/review suggestions only and must target already visible functional agents/surfaces with required capabilities present above.
        - Do not acknowledge, dismiss, resolve, expire, mutate, or create source attention items; human acceptance of this digest is separate advisory review only.
        - Omit raw JWTs, provider secrets, API keys, raw/hidden prompts, invitation tokens, raw tool payloads, and cross-tenant data.
        - If provider/model/runtime/tool/evidence config is unavailable, fail closed with an actionable reason instead of fabricating deterministic, fake, or model-less digest success.
        """.formatted(
            request.digestTaskId(),
            request.tenantId(),
            request.customerId() == null ? "" : request.customerId(),
            request.accountId(),
            request.selectedContextId(),
            request.idempotencyKey(),
            request.correlationId(),
            request.governedRuntimeContext(),
            request.evidenceItems(),
            String.join(", ", request.evidenceRefs()),
            String.join(", ", request.visibleCapabilityIds())));
  }

  public record PersonalAttentionDigestRequest(
      String digestTaskId,
      String tenantId,
      String customerId,
      String accountId,
      String selectedContextId,
      List<PersonalAttentionEvidenceItem> evidenceItems,
      List<String> evidenceRefs,
      List<String> visibleCapabilityIds,
      String idempotencyKey,
      String correlationId,
      String governedRuntimeContext) {
    public PersonalAttentionDigestRequest {
      evidenceItems = List.copyOf(evidenceItems == null ? List.of() : evidenceItems);
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
      visibleCapabilityIds = List.copyOf(visibleCapabilityIds == null ? List.of() : visibleCapabilityIds);
    }
  }

  public record PersonalAttentionEvidenceItem(
      String evidenceId,
      String attentionItemId,
      String sourceWorkstreamId,
      String label,
      String redactedSummary,
      String status,
      String severity,
      String category,
      String requiredCapabilityId,
      String surfaceRefId,
      String redactionLevel,
      List<String> traceRefs) {
    public PersonalAttentionEvidenceItem {
      traceRefs = List.copyOf(traceRefs == null ? List.of() : traceRefs);
    }
  }
}
