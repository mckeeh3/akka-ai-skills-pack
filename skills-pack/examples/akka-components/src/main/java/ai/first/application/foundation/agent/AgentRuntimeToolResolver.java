package ai.first.application.foundation.agent;

import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
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
    if (!request.tenantId().equals(request.authContext().tenantId())) {
      throw new AuthorizationException(403, "runtime-tool-tenant-mismatch");
    }
    var agent = repository.agentDefinition(request.tenantId(), request.agentDefinitionId())
        .orElseThrow(() -> new AuthorizationException(404, "agent-not-found"));
    if (agent.status() != AgentLifecycleStatus.ACTIVE && "runtime".equalsIgnoreCase(request.mode())) {
      throw new AuthorizationException(403, "agent-not-active");
    }
    var boundary = repository.toolBoundary(request.tenantId(), agent.toolBoundaryId())
        .orElseThrow(() -> new AuthorizationException(404, "boundary-not-found"));
    if (boundary.status() != AgentLifecycleStatus.ACTIVE) {
      throw new AuthorizationException(403, "boundary-not-active");
    }
    if (!agent.agentDefinitionId().equals(boundary.agentDefinitionId())) {
      throw new AuthorizationException(403, "boundary-agent-mismatch");
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
        continue;
      }
      var entry = registered.get().entry();
      if (!grantMatchesEntry(grant, entry) || !modeAllowed(grant, request.mode()) || !readOperationAllowed(grant)) {
        deniedToolIds.add(grant.toolId());
        continue;
      }
      grantedToolIds.add(grant.toolId());
      entries.add(entry);
      var bindingGroup = bindingGroup(entry);
      if (!registeredBindingGroups.contains(bindingGroup)) {
        registeredBindingGroups.add(bindingGroup);
        runtimeTools.add(registered.get().createBinding(new ToolRegistry.BindingContext(runtimeService, repository, request.tenantId(), request.agentDefinitionId(), request.authContext(), request.mode(), request.capabilityId(), request.correlationId())));
      }
    }
    return new ResolvedRuntimeTools(List.copyOf(runtimeTools), List.copyOf(entries), List.copyOf(grantedToolIds), List.copyOf(deniedToolIds));
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
