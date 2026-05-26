package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import {{JAVA_BASE_PACKAGE}}.application.security.AuthorizationException;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolCatalogEntry;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Resolves active governed ToolPermissionBoundary grants into Java objects for effects().tools(runtimeTools). */
public final class AgentRuntimeToolResolver {
  private final AgentBehaviorRepository repository;
  private final ToolRegistry toolRegistry;

  public AgentRuntimeToolResolver(AgentBehaviorRepository repository) {
    this(repository, ToolRegistry.starterDefault());
  }

  public AgentRuntimeToolResolver(AgentBehaviorRepository repository, ToolRegistry toolRegistry) {
    this.repository = repository;
    this.toolRegistry = toolRegistry;
  }

  public ResolvedRuntimeTools resolve(ResolveRuntimeToolsRequest request) {
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
      runtimeTools.add(registered.get().createBinding(new ToolRegistry.BindingContext(request.tenantId(), request.agentDefinitionId(), request.mode(), request.correlationId())));
    }
    return new ResolvedRuntimeTools(List.copyOf(runtimeTools), List.copyOf(entries), List.copyOf(grantedToolIds), List.copyOf(deniedToolIds));
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

  public record ResolveRuntimeToolsRequest(String tenantId, String agentDefinitionId, String mode, String correlationId) {}
  public record ResolvedRuntimeTools(List<Object> runtimeTools, List<ToolCatalogEntry> entries, List<String> grantedToolIds, List<String> deniedToolIds) {
    public ResolvedRuntimeTools {
      runtimeTools = List.copyOf(runtimeTools == null ? List.of() : runtimeTools);
      entries = List.copyOf(entries == null ? List.of() : entries);
      grantedToolIds = List.copyOf(grantedToolIds == null ? List.of() : grantedToolIds);
      deniedToolIds = List.copyOf(deniedToolIds == null ? List.of() : deniedToolIds);
    }
  }
}
