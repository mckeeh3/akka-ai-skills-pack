package ai.first.application.coreapp.agentadmin;

import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import ai.first.domain.foundation.identity.AuthContext;
import java.util.List;

/** Runtime seam for Agent Admin AI-assisted document editing. Normal runtime must use the Akka Agent component path. */
public interface AgentAdminDocEditingRuntime {
  EditProposalResult proposeEdit(EditProposalRequest request);

  record EditProposalRequest(
      String runtimeTenantId,
      AuthContext actorContext,
      String actorAccountId,
      String targetAgentDefinitionId,
      String targetAgentName,
      AgentAdminDocAdministrationService.AgentDocKind documentKind,
      String documentId,
      int baseVersion,
      String currentDocumentMarkdown,
      String sameAgentContextMarkdown,
      List<String> userInstructions,
      String priorProposedMarkdown,
      String correlationId,
      List<String> existingTraceIds) {
    public EditProposalRequest {
      userInstructions = List.copyOf(userInstructions == null ? List.of() : userInstructions);
      existingTraceIds = List.copyOf(existingTraceIds == null ? List.of() : existingTraceIds);
    }
  }

  record EditProposalResult(
      AgentRuntimeTrace.Decision decision,
      AgentAdminDocEditingAgent.EditProposal proposal,
      List<String> traceIds,
      String safeErrorCode,
      String safeErrorSummary) {
    public EditProposalResult {
      traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
    }
  }
}
