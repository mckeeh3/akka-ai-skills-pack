package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.agent.task.Task;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptRiskReviewTask.BehaviorArtifactDelta;
import java.util.List;
import java.util.stream.Collectors;

/** Akka AutonomousAgent task definitions for Agent Admin prompt-risk reviews. */
public final class AgentAdminPromptRiskTasks {
  public static final Task<PromptRiskAutonomousAgentResult> PROMPT_RISK_REVIEW = Task
      .name("AgentAdminPromptRiskReview")
      .description("Review proposed managed-agent behavior changes and produce advisory prompt-risk findings for human Agent Admin review")
      .resultConformsTo(PromptRiskAutonomousAgentResult.class)
      .rules(PromptRiskAutonomousAgentResultRule.class);

  private AgentAdminPromptRiskTasks() {}

  public static Task<PromptRiskAutonomousAgentResult> promptRiskInstructions(PromptRiskReviewRequest request) {
    return PROMPT_RISK_REVIEW.instructions("""
        Run a governed Agent Admin prompt-risk review for a managed-agent behavior proposal.

        Scope:
        - starterTaskId: %s
        - tenantId: %s
        - customerId: %s
        - targetAgentDefinitionId: %s
        - proposalId: %s
        - startedByAccountId: %s
        - correlationId: %s
        - governedCapability: %s

        Proposed artifact deltas:
        %s

        Governed runtime context:
        %s

        Evidence/tool references available for this first slice:
        %s

        Required output:
        - Return only the structured PromptRiskAutonomousAgentResult.
        - taskId, tenantId, customerId, targetAgentDefinitionId, and proposalId must match the scope above.
        - Findings must cover all proposed prompt, skill, reference, model, and tool-boundary artifact kinds present in the delta list.
        - Findings and recommendations are advisory only; do not claim prompt, skill, reference, model policy, ToolPermissionBoundary, or AgentDefinition activation, rollback, reseeding, editing, or approval.
        - Include evidenceRefs and traceIds for governed prompt assembly, skill/reference loads, model invocation, proposal reads, and work traces used.
        - safety must explicitly state that Agent Admin human review is required before activation or behavior changes.
        - If provider, policy, tool grants, target artifacts, or evidence is unavailable, fail the task with an actionable reason instead of fabricating deterministic/model-less findings.
        """.formatted(
            request.taskId(),
            request.tenantId(),
            request.customerId() == null ? "" : request.customerId(),
            request.targetAgentDefinitionId(),
            request.proposalId(),
            request.startedByAccountId(),
            request.correlationId(),
            request.capabilityId(),
            request.proposedDeltas().stream().map(AgentAdminPromptRiskTasks::deltaSummary).collect(Collectors.joining("\n")),
            request.governedRuntimeContext(),
            String.join(", ", request.evidenceRefs())));
  }

  private static String deltaSummary(BehaviorArtifactDelta delta) {
    return "- " + delta.artifactKind() + ":" + delta.artifactId() + " from=" + delta.fromVersion() + " to=" + delta.toVersion() + " summary=" + safe(delta.changeSummary()) + " redactedDiffRef=" + safe(delta.redactedDiffRef());
  }

  private static String safe(String value) {
    return value == null ? "" : value.replaceAll("(?i)(api[_-]?key|secret|token)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
  }

  public record PromptRiskReviewRequest(
      String taskId,
      String tenantId,
      String customerId,
      String targetAgentDefinitionId,
      String proposalId,
      String startedByAccountId,
      String correlationId,
      String capabilityId,
      String governedRuntimeContext,
      List<BehaviorArtifactDelta> proposedDeltas,
      List<String> evidenceRefs) {
    public PromptRiskReviewRequest {
      proposedDeltas = List.copyOf(proposedDeltas == null ? List.of() : proposedDeltas);
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    }
  }
}
