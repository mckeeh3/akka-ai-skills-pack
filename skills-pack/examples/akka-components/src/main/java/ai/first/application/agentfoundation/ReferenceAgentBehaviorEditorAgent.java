package ai.first.application.agentfoundation;

import akka.javasdk.agent.Agent;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Description;
import java.util.List;

/**
 * Minimal deterministic AgentBehaviorEditorAgent wrapper reference.
 *
 * <p>The Java Agent returns structured proposal intent only. Backend helpers such as {@link
 * ReferenceAgentBehaviorEditor} remain responsible for authorization, artifact lookup, risk enforcement, trace
 * emission, and draft/proposal creation. This wrapper never mutates active runtime behavior directly.
 */
@Component(
    id = "reference-agent-behavior-editor-agent",
    name = "Reference Agent Behavior Editor Agent",
    description = "Drafts structured behavior-edit proposal intent for governed prompt, skill, manifest, and tool-boundary changes.")
public class ReferenceAgentBehaviorEditorAgent extends Agent {

  private static final String SYSTEM_MESSAGE =
      """
      You are a governed AgentBehaviorEditorAgent reference wrapper.
      Return only a structured behavior edit proposal intent.
      Identify the likely target artifact, summarize the requested proposed diff, and classify risk.
      Do not claim that prompt or skill text can grant tool, data, approval, tenant, role, model, or policy authority.
      Do not mutate active prompt, skill, manifest, tool-boundary, policy, model, or AgentDefinition records.
      Authority expansion must be marked for decision-card review or denial by backend governance.
      """
          .stripIndent();

  public record BehaviorEditDraftRequest(
      @Description("Tenant id from the authorized AuthContext") String tenantId,
      @Description("Client or server behavior change request id") String requestId,
      @Description("Account id that requested the behavior edit") String requestedByAccountId,
      @Description("Target AgentDefinition id") String targetAgentDefinitionId,
      @Description("Natural language behavior change request") String requestedChange,
      @Description("Optional known artifact type: prompt, skill, manifest, tool_boundary, agent_definition, or unknown")
          String targetArtifactHint,
      @Description("Optional known artifact id; backend validates it before proposal creation") String targetArtifactId,
      @Description("Correlation id for trace linkage") String correlationId) {}

  public record BehaviorEditProposalIntent(
      @Description("Same tenant id from the request") String tenantId,
      @Description("Same request id from the request") String requestId,
      @Description("Target AgentDefinition id") String targetAgentDefinitionId,
      @Description("Selected artifact type for backend validation") String targetArtifactType,
      @Description("Selected artifact id for backend validation") String targetArtifactId,
      @Description("One sentence proposed diff summary; not an active mutation") String proposedDiffSummary,
      @Description("Risk classification: low, medium, high, or blocked") String riskClassification,
      @Description("Whether the request appears to expand authority") boolean authorityExpansionDetected,
      @Description("Authority expansion types such as tool, data, approval, autonomy, billing, model, tenant_scope")
          List<String> expansionTypes,
      @Description("Whether review should use a decision card") boolean decisionCardRequired,
      @Description("Recommended next action: create_draft, create_decision_card, deny, or ask_clarification")
          String recommendedNextAction,
      @Description("Safe rationale for reviewers; do not include secrets or hidden policy text") String rationale,
      @Description("Correlation id for trace linkage") String correlationId) {

    public BehaviorEditProposalIntent {
      expansionTypes = List.copyOf(expansionTypes);
    }
  }

  public Effect<BehaviorEditProposalIntent> draftProposal(BehaviorEditDraftRequest request) {
    return effects()
        .systemMessage(SYSTEM_MESSAGE)
        .userMessage(userMessage(request))
        .responseConformsTo(BehaviorEditProposalIntent.class)
        .thenReply();
  }

  private static String userMessage(BehaviorEditDraftRequest request) {
    return """
        Behavior edit request:
        tenantId: %s
        requestId: %s
        requestedByAccountId: %s
        targetAgentDefinitionId: %s
        targetArtifactHint: %s
        targetArtifactId: %s
        requestedChange: %s
        correlationId: %s

        Produce structured proposal intent only. Backend governance will validate tenant, permissions, lifecycle,
        affected artifact ids, draft/proposal creation, review routing, and trace emission.
        """
        .formatted(
            request.tenantId(),
            request.requestId(),
            request.requestedByAccountId(),
            request.targetAgentDefinitionId(),
            request.targetArtifactHint(),
            request.targetArtifactId(),
            request.requestedChange(),
            request.correlationId());
  }
}
