package ai.first.application.coreapp.useradmin;

import akka.javasdk.agent.task.Task;
import java.util.List;
import java.util.stream.Collectors;

/** Akka AutonomousAgent task definitions for the User Admin access-review vertical. */
public final class UserAdminAccessReviewTasks {
  public static final Task<AccessReviewAutonomousAgentResult> ACCESS_REVIEW = Task
      .name("UserAdminAccessReview")
      .description("Investigate tenant/customer user access and produce advisory recommendations for human review")
      .resultConformsTo(AccessReviewAutonomousAgentResult.class)
      .rules(AccessReviewAutonomousAgentResultRule.class);

  private UserAdminAccessReviewTasks() {}

  public static Task<AccessReviewAutonomousAgentResult> accessReviewInstructions(AccessReviewAutonomousAgentRequest request) {
    return ACCESS_REVIEW.instructions("""
        Run a governed User Admin access-review investigation.

        Scope:
        - starterTaskId: %s
        - tenantId: %s
        - customerId: %s
        - scopeType: %s
        - startedByAccountId: %s
        - correlationId: %s
        - governedCapability: %s

        Governed runtime context:
        %s

        Evidence/tool references available for this first slice:
        %s

        Required output:
        - Return only the structured AccessReviewAutonomousAgentResult.
        - taskId, tenantId, and customerId must match the scope above.
        - Recommendations are advisory only; do not claim any membership, invitation, role, capability, tenant/customer, provider, or audit-policy mutation has been performed.
        - Include evidenceRefs and traceIds for the governed prompt/skill/reference/work traces used.
        - safety must explicitly state that a User Admin human must review and accept/reject before follow-up access changes.
        - If provider, policy, or evidence is unavailable, fail the task with an actionable reason instead of fabricating deterministic/model-less recommendations.
        """.formatted(
            request.starterTaskId(),
            request.tenantId(),
            request.customerId() == null ? "" : request.customerId(),
            request.scopeType(),
            request.startedByAccountId(),
            request.correlationId(),
            request.capabilityId(),
            request.governedRuntimeContext(),
            request.evidenceRefs().stream().collect(Collectors.joining(", "))));
  }

  public record AccessReviewAutonomousAgentRequest(
      String starterTaskId,
      String tenantId,
      String customerId,
      String scopeType,
      String startedByAccountId,
      String correlationId,
      String capabilityId,
      String governedRuntimeContext,
      List<String> evidenceRefs) {
    public AccessReviewAutonomousAgentRequest {
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    }
  }
}
