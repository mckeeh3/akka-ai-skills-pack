package ai.first.application.foundation.agent;

import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import ai.first.domain.foundation.agent.AgentBehaviorProfileVersion;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import ai.first.domain.foundation.agent.ToolCatalogEntry;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import ai.first.domain.foundation.identity.AuthContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Resolves active governed ToolPermissionBoundary grants into Java objects for effects().tools(runtimeTools). */
public final class AgentRuntimeToolResolver {
  private final AgentBehaviorRepository repository;
  private final AgentRuntimeService runtimeService;
  private final ToolRegistry toolRegistry;

  public AgentRuntimeToolResolver(AgentBehaviorRepository repository, AgentRuntimeService runtimeService) {
    this(repository, runtimeService, ToolRegistry.starterDefault());
  }

  public AgentRuntimeToolResolver(AgentBehaviorRepository repository, AgentRuntimeService runtimeService, ToolRegistry toolRegistry) {
    this.repository = repository;
    this.runtimeService = runtimeService;
    this.toolRegistry = toolRegistry;
  }

  public ResolvedRuntimeTools resolve(ResolveRuntimeToolsRequest request) {
    if (request == null) {
      throw new AuthorizationException(400, "runtime-tool-context-required");
    }
    if (request.authContext() == null) {
      throw new AuthorizationException(403, "runtime-tool-auth-context-required");
    }
    if (isBlank(request.tenantId()) || isBlank(request.agentDefinitionId()) || isBlank(request.mode()) || isBlank(request.capabilityId()) || isBlank(request.correlationId())) {
      throw new AuthorizationException(403, "runtime-tool-context-incomplete");
    }
    if (!runtimeScopeMatchesAuthContext(request.tenantId(), request.authContext())) {
      throw new AuthorizationException(403, "runtime-tool-tenant-mismatch");
    }
    var agent = repository.agentDefinition(request.tenantId(), request.agentDefinitionId())
        .or(() -> platformScopeId().equals(request.tenantId()) ? java.util.Optional.empty() : repository.agentDefinition(platformScopeId(), request.agentDefinitionId()))
        .orElseThrow(() -> new AuthorizationException(404, "agent-not-found"));
    if (agent.status() != AgentLifecycleStatus.ACTIVE && "runtime".equalsIgnoreCase(request.mode())) {
      throw new AuthorizationException(403, "agent-not-active");
    }
    var profile = activeProfile(request.tenantId(), request.agentDefinitionId())
        .or(() -> platformScopeId().equals(request.tenantId()) ? java.util.Optional.empty() : activeProfile(platformScopeId(), request.agentDefinitionId()))
        .orElseThrow(() -> new AuthorizationException(403, "behavior-profile-not-active"));
    var boundary = boundaryForProfile(request.tenantId(), profile)
        .orElseThrow(() -> new AuthorizationException(404, "boundary-not-found"));
    if (boundary.status() != AgentLifecycleStatus.ACTIVE) {
      throw new AuthorizationException(403, "boundary-not-active");
    }
    if (!agent.agentDefinitionId().equals(boundary.agentDefinitionId())) {
      throw new AuthorizationException(403, "boundary-agent-mismatch");
    }
    if (boundary.boundaryVersion() != profile.activeToolBoundaryVersion()) {
      throw new AuthorizationException(403, "boundary-profile-version-mismatch");
    }

    var grantedToolIds = new ArrayList<String>();
    var deniedToolIds = new ArrayList<String>();
    var entries = new ArrayList<ToolCatalogEntry>();
    var runtimeTools = new ArrayList<Object>();
    var registeredBindingGroups = new ArrayList<String>();
    var sortedGrants = boundary.allowedToolGrants().stream()
        .sorted(Comparator.comparing(ToolPermissionBoundary.ToolGrant::toolId))
        .toList();
    for (var grant : sortedGrants) {
      var registered = toolRegistry.find(grant.toolId());
      if (registered.isEmpty()) {
        deniedToolIds.add(grant.toolId());
        recordToolDecision("TOOL_BOUNDARY", AgentRuntimeTrace.Decision.DENIED, request, profile, grant.toolId(), "tool-registry-binding-missing");
        continue;
      }
      var entry = registered.get().entry();
      if (!grantMatchesEntry(grant, entry) || !modeAllowed(grant, request.mode()) || !readOperationAllowed(grant)) {
        deniedToolIds.add(grant.toolId());
        recordToolDecision("TOOL_BOUNDARY", AgentRuntimeTrace.Decision.DENIED, request, profile, grant.toolId(), "tool-boundary-denied");
        continue;
      }
      if (isGeneratedToolGrant(grant) && !profile.assignedGeneratedToolIds().contains(grant.toolId())) {
        deniedToolIds.add(grant.toolId());
        recordToolDecision("GENERATED_TOOL_ASSIGNMENT", AgentRuntimeTrace.Decision.DENIED, request, profile, grant.toolId(), "generated-tool-not-assigned-by-active-profile");
        continue;
      }
      grantedToolIds.add(grant.toolId());
      entries.add(entry);
      if (isGeneratedToolGrant(grant)) {
        recordToolDecision("GENERATED_TOOL_ASSIGNMENT", AgentRuntimeTrace.Decision.ALLOWED, request, profile, grant.toolId(), "generated-tool-assigned-by-active-profile");
      }
      var bindingGroup = bindingGroup(entry);
      if (!registeredBindingGroups.contains(bindingGroup)) {
        registeredBindingGroups.add(bindingGroup);
        runtimeTools.add(registered.get().createBinding(new ToolRegistry.BindingContext(runtimeService, repository, request.tenantId(), request.agentDefinitionId(), request.authContext(), request.mode(), request.capabilityId(), request.correlationId())));
      }
    }
    return new ResolvedRuntimeTools(List.copyOf(runtimeTools), List.copyOf(entries), List.copyOf(grantedToolIds), List.copyOf(deniedToolIds));
  }

  private java.util.Optional<AgentBehaviorProfileVersion> activeProfile(String tenantId, String agentDefinitionId) {
    try {
      return repository.activeBehaviorProfile(tenantId, agentDefinitionId);
    } catch (UnsupportedOperationException failure) {
      throw new AuthorizationException(403, "behavior-profile-store-not-bound");
    }
  }

  private java.util.Optional<ToolPermissionBoundary> boundaryForProfile(String requestTenantId, AgentBehaviorProfileVersion profile) {
    for (var scope : resourceScopes(requestTenantId, profile)) {
      var boundary = repository.toolBoundary(scope, profile.toolBoundaryId());
      if (boundary.isPresent()) {
        return boundary;
      }
    }
    return java.util.Optional.empty();
  }

  private List<String> resourceScopes(String requestTenantId, AgentBehaviorProfileVersion profile) {
    var scopes = new ArrayList<String>();
    addScope(scopes, profile.tenantId());
    addScope(scopes, profile.clonedFromTenantId());
    addScope(scopes, requestTenantId);
    addScope(scopes, platformScopeId());
    return List.copyOf(scopes);
  }

  private void addScope(List<String> scopes, String scope) {
    if (!isBlank(scope) && !scopes.contains(scope)) {
      scopes.add(scope);
    }
  }

  private boolean isGeneratedToolGrant(ToolPermissionBoundary.ToolGrant grant) {
    return grant.category() != ToolPermissionBoundary.Category.READ_SKILL
        && grant.category() != ToolPermissionBoundary.Category.READ_REFERENCE;
  }

  private void recordToolDecision(String traceType, AgentRuntimeTrace.Decision decision, ResolveRuntimeToolsRequest request, AgentBehaviorProfileVersion profile, String toolId, String reason) {
    runtimeService.recordRuntimeToolDecision(
        traceType,
        decision,
        request.tenantId(),
        request.agentDefinitionId(),
        request.authContext(),
        request.capabilityId(),
        request.correlationId(),
        toolId,
        reason + "; behaviorProfileScope=" + profileScopeLabel(profile) + "; behaviorProfileVersion=" + profile.profileVersion() + "; toolBoundary=" + profile.toolBoundaryId() + "@" + profile.activeToolBoundaryVersion(),
        AgentRuntimeService.checksum(String.valueOf(toolId) + reason + profile.profileChecksum()));
  }

  private String profileScopeLabel(AgentBehaviorProfileVersion profile) {
    return profile.scopeProvenance() == AgentBehaviorProfileVersion.ScopeProvenance.GLOBAL_DEFAULT ? "global" : "tenant:" + profile.tenantId();
  }

  private String platformScopeId() {
    return WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID;
  }

  private boolean runtimeScopeMatchesAuthContext(String runtimeGovernanceScopeId, AuthContext authContext) {
    if (authContext.scopeType() == ai.first.domain.foundation.identity.ScopeType.SAAS_OWNER) {
      return WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID.equals(runtimeGovernanceScopeId)
          && (authContext.tenantId() == null || authContext.tenantId().isBlank());
    }
    return runtimeGovernanceScopeId.equals(authContext.tenantId());
  }

  private String bindingGroup(ToolCatalogEntry entry) {
    return entry.implementationBindingKey().startsWith("governed-loader.") ? "governed-loader" : entry.implementationBindingKey();
  }

  private boolean grantMatchesEntry(ToolPermissionBoundary.ToolGrant grant, ToolCatalogEntry entry) {
    return grant.category() == entry.category() && entry.capabilityId().equals(grant.capabilityId());
  }

  private boolean modeAllowed(ToolPermissionBoundary.ToolGrant grant, String mode) {
    return grant.allowedModes().stream().anyMatch(allowedMode -> allowedMode.equalsIgnoreCase(mode));
  }

  private boolean readOperationAllowed(ToolPermissionBoundary.ToolGrant grant) {
    return grant.allowedOperations().stream().anyMatch(operation -> operation.equalsIgnoreCase("read"));
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  public record ResolveRuntimeToolsRequest(String tenantId, String agentDefinitionId, AuthContext authContext, String mode, String capabilityId, String correlationId) {
    public ResolveRuntimeToolsRequest(String tenantId, String agentDefinitionId, String mode, String correlationId) {
      this(tenantId, agentDefinitionId, null, mode, AgentRuntimeService.INVOKE_CAPABILITY, correlationId);
    }
  }
  public record ResolvedRuntimeTools(List<Object> runtimeTools, List<ToolCatalogEntry> entries, List<String> grantedToolIds, List<String> deniedToolIds) {
    public ResolvedRuntimeTools {
      runtimeTools = List.copyOf(runtimeTools == null ? List.of() : runtimeTools);
      entries = List.copyOf(entries == null ? List.of() : entries);
      grantedToolIds = List.copyOf(grantedToolIds == null ? List.of() : grantedToolIds);
      deniedToolIds = List.copyOf(deniedToolIds == null ? List.of() : deniedToolIds);
    }
  }
}
