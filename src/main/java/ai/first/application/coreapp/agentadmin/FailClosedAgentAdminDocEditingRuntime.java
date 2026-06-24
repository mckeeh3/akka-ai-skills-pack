package ai.first.application.coreapp.agentadmin;

import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import java.util.List;

/** Fail-closed placeholder used when Agent Admin document editing has no Akka Agent runtime binding. */
public final class FailClosedAgentAdminDocEditingRuntime implements AgentAdminDocEditingRuntime {
  @Override
  public EditProposalResult proposeEdit(EditProposalRequest request) {
    return new EditProposalResult(
        AgentRuntimeTrace.Decision.DENIED,
        null,
        List.of("trace-agent-admin-doc-editing-runtime-missing"),
        "AGENT_ADMIN_DOC_EDITING_RUNTIME_REQUIRED",
        "Agent Admin document editing requires the Akka Agent ComponentClient runtime path and configured model provider.");
  }
}
