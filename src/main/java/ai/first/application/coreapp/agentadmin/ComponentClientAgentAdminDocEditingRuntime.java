package ai.first.application.coreapp.agentadmin;

import ai.first.application.foundation.agent.AgentBehaviorRepository;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;
import ai.first.application.foundation.agent.AgentRuntimeService;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import akka.javasdk.client.ComponentClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Production Agent Admin doc-editing runtime that invokes the concrete Akka Agent component. */
public final class ComponentClientAgentAdminDocEditingRuntime implements AgentAdminDocEditingRuntime {
  private final AgentBehaviorRepository repository;
  private final AgentRuntimeService agentRuntimeService;
  private final ComponentClient componentClient;

  public ComponentClientAgentAdminDocEditingRuntime(
      AgentBehaviorRepository repository,
      AgentRuntimeService agentRuntimeService,
      ComponentClient componentClient) {
    this.repository = Objects.requireNonNull(repository);
    this.agentRuntimeService = Objects.requireNonNull(agentRuntimeService);
    this.componentClient = Objects.requireNonNull(componentClient);
  }

  @Override
  public EditProposalResult proposeEdit(EditProposalRequest request) {
    var prompt = agentRuntimeService.assemblePrompt(new AgentRuntimeService.PromptAssemblyRequest(
        request.runtimeTenantId(),
        AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
        request.actorContext(),
        "runtime",
        AgentRuntimeService.AGENT_ADMIN_DRAFT_BEHAVIOR_CHANGE_CAPABILITY,
        request.correlationId(),
        String.join("\n", request.userInstructions())));
    var traceIds = new ArrayList<>(request.existingTraceIds());
    traceIds.add(prompt.traceId());
    if (prompt.decision() != AgentRuntimeTrace.Decision.ALLOWED) {
      return new EditProposalResult(
          AgentRuntimeTrace.Decision.DENIED,
          null,
          traceIds,
          "AGENT_ADMIN_DOC_EDITING_RUNTIME_DENIED",
          prompt.safeDenialReason());
    }

    try {
      var agent = repository.agentDefinition(request.runtimeTenantId(), AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID)
          .orElseThrow(() -> new IllegalStateException("editing-agent-definition-not-found"));
      var model = repository.modelConfigRef(request.runtimeTenantId(), agent.modelConfigRefId())
          .orElseThrow(() -> new IllegalStateException("editing-agent-model-config-not-found"));
      var proposal = componentClient
          .forAgent()
          .inSession("agent-admin-doc-edit-" + safeSessionPart(request.actorContext().membershipId()) + "-" + safeSessionPart(request.documentId()))
          .method(AgentAdminDocEditingAgent::proposeEdit)
          .invoke(new AgentAdminDocEditingAgent.GovernedDocEditRequest(
              prompt.assembledSystemPrompt(),
              model.providerAlias(),
              request.correlationId(),
              AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
              request.targetAgentDefinitionId(),
              request.targetAgentName(),
              request.documentKind().name(),
              request.documentId(),
              request.baseVersion(),
              request.currentDocumentMarkdown(),
              request.sameAgentContextMarkdown(),
              request.userInstructions(),
              request.priorProposedMarkdown(),
              traceIds));
      return new EditProposalResult(AgentRuntimeTrace.Decision.ALLOWED, proposal, traceIds, null, null);
    } catch (RuntimeException failure) {
      return new EditProposalResult(
          AgentRuntimeTrace.Decision.DENIED,
          null,
          traceIds,
          "AGENT_ADMIN_DOC_EDITING_AGENT_INVOCATION_FAILED",
          safeReason(failure));
    }
  }

  public static ComponentClientAgentAdminDocEditingRuntime platformRuntime(
      AgentBehaviorRepository repository,
      AgentRuntimeService agentRuntimeService,
      ComponentClient componentClient) {
    return new ComponentClientAgentAdminDocEditingRuntime(repository, agentRuntimeService, componentClient);
  }

  static String platformTenantId() {
    return WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID;
  }

  private static String safeSessionPart(String value) {
    return String.valueOf(value).replaceAll("[^A-Za-z0-9_.-]+", "-");
  }

  private static String safeReason(Throwable failure) {
    var message = failure == null ? "editing-agent-runtime-denied" : String.valueOf(failure.getMessage());
    return message.replaceAll("(?i)(api[_-]?key|secret|token)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
  }
}
