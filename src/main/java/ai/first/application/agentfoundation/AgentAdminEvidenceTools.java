package ai.first.application.agentfoundation;

import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.FunctionTool;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.ModelConfigRef;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import ai.first.domain.foundation.identity.AuthContext;
import java.util.Objects;
import ai.first.application.foundation.agent.AgentBehaviorRepository;

/** Request-scoped read-only Agent Admin evidence facade for AgentAdminAgent. */
public final class AgentAdminEvidenceTools {
  public static final String TOOL_ID = "agentAdminEvidence.read";
  public static final String CAPABILITY_ID = AgentAdminService.LIST_DEFINITIONS;

  private final AgentBehaviorRepository repository;
  private final AuthContext authContext;
  private final String correlationId;

  public AgentAdminEvidenceTools(AgentBehaviorRepository repository, AuthContext authContext, String correlationId) {
    this.repository = Objects.requireNonNull(repository);
    this.authContext = Objects.requireNonNull(authContext);
    this.correlationId = correlationId == null || correlationId.isBlank() ? "agent-admin-evidence" : correlationId;
  }

  @FunctionTool(description = """
      Read scoped, browser-safe Agent Admin evidence for the selected AuthContext.
      This is a read-only DATA_LOOKUP tool: it summarizes active AgentDefinitions, prompt/skill/reference manifests,
      model/provider readiness, ToolPermissionBoundary grants, seed provenance, proposal lifecycle cues, and trace ids.
      It cannot approve, activate, rollback, reseed, mutate model refs, edit prompts/skills/references, or change tool boundaries.
      It enforces selected tenant scope and agent_admin.list_definitions before returning data.
      """)
  public String read(@Description("Optional plain-language evidence focus; may include tenantId=<id> only for the selected tenant") String evidenceRequest) {
    requireCapability();
    requireRequestedScope(evidenceRequest);
    var tenantId = authContext.tenantId();
    var agents = repository.agentDefinitions(tenantId);
    var definitions = agents.stream()
        .limit(10)
        .map(agent -> {
          var model = repository.modelConfigRef(tenantId, agent.modelConfigRefId()).orElse(null);
          var prompt = repository.promptDocument(tenantId, agent.promptDocumentId()).orElse(null);
          var skillManifest = repository.skillManifest(tenantId, agent.skillManifestId()).orElse(null);
          var referenceManifest = repository.referenceManifest(tenantId, agent.referenceManifestId()).orElse(null);
          var boundary = repository.toolBoundary(tenantId, agent.toolBoundaryId()).orElse(null);
          return "{agentDefinitionId=" + safe(agent.agentDefinitionId())
              + ", displayName=" + safe(agent.displayName())
              + ", status=" + agent.status()
              + ", authorityLevel=" + agent.authorityLevel()
              + ", functionalAreaId=" + safe(agent.functionalAreaId())
              + ", prompt=" + safe(agent.promptDocumentId()) + "@" + agent.activePromptVersion()
              + ", promptStatus=" + (prompt == null ? "missing" : prompt.status())
              + ", skillManifest=" + safe(agent.skillManifestId()) + "@" + agent.activeSkillManifestVersion()
              + ", skillCount=" + (skillManifest == null ? 0 : skillManifest.entries().size())
              + ", referenceManifest=" + safe(agent.referenceManifestId()) + "@" + agent.activeReferenceManifestVersion()
              + ", referenceCount=" + (referenceManifest == null ? 0 : referenceManifest.entries().size())
              + ", toolBoundary=" + safe(agent.toolBoundaryId()) + "@" + agent.activeToolBoundaryVersion()
              + ", toolGrants=" + (boundary == null ? "missing" : boundary.allowedToolGrants().stream().map(ToolPermissionBoundary.ToolGrant::toolId).toList())
              + ", providerReadiness=" + providerReadiness(model)
              + ", seed=" + (agent.seedProvenance() == null ? "unknown" : safe(agent.seedProvenance().seedBundleId()) + ":" + safe(agent.seedProvenance().contentVersion()))
              + ", traceId=trace-agentadmin-evidence-definition-" + stableSuffix(agent.agentDefinitionId())
              + "}";
        })
        .toList();
    var boundarySummaries = agents.stream()
        .limit(10)
        .map(agent -> repository.toolBoundary(tenantId, agent.toolBoundaryId())
            .map(boundary -> "{boundaryId=" + safe(boundary.boundaryId())
                + ", agentDefinitionId=" + safe(agent.agentDefinitionId())
                + ", status=" + boundary.status()
                + ", readOnlyGrantCount=" + boundary.allowedToolGrants().stream().filter(grant -> "none".equalsIgnoreCase(grant.sideEffectLevel())).count()
                + ", grants=" + boundary.allowedToolGrants().stream().map(grant -> grant.toolId() + ":" + grant.capabilityId() + ":" + grant.category()).toList()
                + ", noDirectMutation=true}"
            ).orElse("{boundaryId=" + safe(agent.toolBoundaryId()) + ", status=missing}"))
        .toList();
    var traceId = "trace-agentadmin-evidence-" + stableSuffix(correlationId + ":" + authContext.membershipId());
    return "tool_id=" + TOOL_ID
        + "\ncapability=" + CAPABILITY_ID
        + "\nmode=read_only_no_direct_mutation"
        + "\nselectedTenantId=" + safe(tenantId)
        + "\nselectedCustomerId=" + safe(authContext.customerId())
        + "\nagentDefinitionCount=" + agents.size()
        + "\ndefinitions=" + definitions
        + "\ntoolBoundaries=" + boundarySummaries
        + "\nproposalGuidance=Guidance may draft rationale or next steps only; deterministic backend proposal/review/activate/rollback commands own behavior changes."
        + "\nproviderGuidance=Provider aliases are browser-safe summaries only; provider credentials and hidden model configuration are redacted."
        + "\ntraceId=" + traceId
        + "\nredaction=raw prompt bodies, skill/reference full text, provider credentials, JWTs, support-only data, and cross-tenant data omitted"
        + "\nauthority_note=Evidence is scoped deterministic data only; AgentAdminAgent has no direct mutation, approval, activation, rollback, reseed, model-ref, or tool-boundary authority.";
  }

  private void requireCapability() {
    if (!authContext.hasCapability(CAPABILITY_ID)) {
      throw new AuthorizationException(403, "missing-capability:" + CAPABILITY_ID);
    }
  }

  private void requireRequestedScope(String evidenceRequest) {
    if (evidenceRequest == null) return;
    var marker = "tenantId=";
    var index = evidenceRequest.indexOf(marker);
    if (index < 0) return;
    var requested = evidenceRequest.substring(index + marker.length()).split("[\\s,;]")[0].trim();
    if (!requested.isBlank() && !requested.equals(authContext.tenantId())) {
      throw new AuthorizationException(403, "agent-admin-evidence-tenant-mismatch");
    }
  }

  private String providerReadiness(ModelConfigRef model) {
    if (model == null) return "blocked_provider_or_runtime:model-ref-missing";
    if (model.status() != AgentLifecycleStatus.ACTIVE) return "blocked_provider_or_runtime:model-ref-not-active:" + safe(model.providerAlias());
    return "ready:" + safe(model.providerAlias()) + ":credentials-redacted";
  }

  private static String safe(String value) {
    if (value == null) return "null";
    return value.replaceAll("(?i)(api[_-]?key|secret|token|credential)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "agent-admin-evidence").hashCode(), 36);
  }
}
