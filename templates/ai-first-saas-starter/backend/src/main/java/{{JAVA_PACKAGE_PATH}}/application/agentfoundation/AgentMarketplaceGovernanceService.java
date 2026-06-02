package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import {{JAVA_BASE_PACKAGE}}.application.security.AuthContextResolver;
import {{JAVA_BASE_PACKAGE}}.application.security.AuthorizationException;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentDefinition;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.BehaviorChangeProposal;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolCatalogEntry;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import {{JAVA_BASE_PACKAGE}}.domain.security.AuthContext;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Governance facade for marketplace prompt imports and tenant-managed tool binding requests.
 *
 * <p>Marketplace content and tenant-selected tool ids only create governed proposals. They never
 * mutate active prompts or active {@link ToolPermissionBoundary} records directly, and tool binding
 * requests can select only backend-owned stable tool ids from {@link ToolRegistry}.
 */
public final class AgentMarketplaceGovernanceService {
  public static final String LIST_MARKETPLACE_PROMPTS_CAPABILITY = "agent_admin.marketplace_prompts.read";
  public static final String IMPORT_MARKETPLACE_PROMPT_CAPABILITY = "agent_admin.marketplace_prompt.import";
  public static final String REQUEST_TOOL_BINDING_CAPABILITY = "agent_admin.tool_binding.request";

  private final AgentBehaviorRepository repository;
  private final AgentRuntimeService runtimeService;
  private final AuthContextResolver authContextResolver;
  private final ToolRegistry toolRegistry;
  private final Clock clock;
  private final List<MarketplacePrompt> marketplacePrompts;

  public AgentMarketplaceGovernanceService(
      AgentBehaviorRepository repository,
      AgentRuntimeService runtimeService,
      AuthContextResolver authContextResolver,
      ToolRegistry toolRegistry,
      Clock clock,
      List<MarketplacePrompt> marketplacePrompts) {
    this.repository = Objects.requireNonNull(repository);
    this.runtimeService = Objects.requireNonNull(runtimeService);
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
    this.toolRegistry = Objects.requireNonNull(toolRegistry);
    this.clock = Objects.requireNonNull(clock);
    this.marketplacePrompts = List.copyOf(marketplacePrompts == null ? List.of() : marketplacePrompts);
  }

  public static List<MarketplacePrompt> starterMarketplacePrompts() {
    return List.of(new MarketplacePrompt(
        "marketplace.agent-admin.safe-steward.v1",
        "Agent Admin Safe Steward",
        "Starter Pack",
        "starter-marketplace-v1",
        "Agent Admin prompt variant that reinforces governed proposals, review gates, and backend-enforced tool boundaries.",
        "You are the Agent Admin safe steward. Draft prompt, skill, reference, manifest, and tool-boundary proposals with explicit risk, evidence, tests, and reviewer scope. Do not claim prompt text can grant backend authority. Keep activation, expanded tools, review gates, secrets, and tenant scope under backend enforcement.",
        List.of("agent-admin", "prompt-governance", "tool-boundary")));
  }

  public MarketplaceCatalogResponse catalog(AuthContext actor, String tenantId, String correlationId) {
    require(actor, tenantId, LIST_MARKETPLACE_PROMPTS_CAPABILITY);
    var entries = marketplacePrompts.stream()
        .map(prompt -> prompt.browserSafe())
        .toList();
    return new MarketplaceCatalogResponse("agent_admin.marketplace_prompt_catalog.v1", entries, traceId("marketplace-catalog", tenantId, correlationId));
  }

  public MarketplaceImportResult proposeMarketplacePromptImport(MarketplacePromptImportRequest request) {
    require(request.actor(), request.tenantId(), IMPORT_MARKETPLACE_PROMPT_CAPABILITY);
    var prompt = marketplacePrompt(request.marketplacePromptId());
    var expected = request.expectedChecksum();
    if (expected == null || expected.isBlank() || !expected.equals(prompt.contentChecksum())) {
      return new MarketplaceImportResult(ImportStatus.DENIED, null, "marketplace-prompt-checksum-mismatch", traceId("marketplace-import-denied", prompt.marketplacePromptId(), request.correlationId()));
    }
    var proposal = runtimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(
        request.tenantId(),
        request.agentDefinitionId(),
        request.actor(),
        BehaviorChangeProposal.TargetArtifact.PROMPT,
        prompt.promptBody(),
        List.of(),
        "Import marketplace prompt " + prompt.marketplacePromptId() + " from " + prompt.publisher() + "; provenance=" + prompt.provenanceRef() + "; checksum=" + prompt.contentChecksum() + "; " + request.rationale(),
        request.correlationId()));
    var status = proposal.status() == BehaviorChangeProposal.Status.DENIED ? ImportStatus.DENIED : ImportStatus.PROPOSED;
    return new MarketplaceImportResult(status, proposal, proposal.reviewReason(), traceId("marketplace-import", proposal.proposalId(), request.correlationId()));
  }

  public ToolBindingRequestResult requestTenantToolBinding(TenantToolBindingRequest request) {
    require(request.actor(), request.tenantId(), REQUEST_TOOL_BINDING_CAPABILITY);
    var agent = agent(request.tenantId(), request.agentDefinitionId());
    var current = repository.toolBoundary(request.tenantId(), agent.toolBoundaryId())
        .orElseThrow(() -> new AuthorizationException(404, "tool-boundary-not-found"));
    var requestedGrants = new ArrayList<ToolPermissionBoundary.ToolGrant>();
    for (var toolId : request.stableToolIds()) {
      var entry = toolRegistry.find(toolId).map(ToolRegistry.RegisteredTool::entry).orElse(null);
      if (entry == null) {
        return ToolBindingRequestResult.denied("unapproved-tool-id:" + toolId, traceId("tool-binding-denied", toolId, request.correlationId()));
      }
      if (entry.category() == ToolPermissionBoundary.Category.EXTERNAL_SIDE_EFFECT
          || entry.sideEffectLevel() == ToolCatalogEntry.SideEffectLevel.BILLING
          || entry.sideEffectLevel() == ToolCatalogEntry.SideEffectLevel.SECURITY
          || entry.sideEffectLevel() == ToolCatalogEntry.SideEffectLevel.IRREVERSIBLE) {
        return ToolBindingRequestResult.denied("high-impact-tool-requires-provider-specific-approval:" + toolId, traceId("tool-binding-denied", toolId, request.correlationId()));
      }
      requestedGrants.add(grantFrom(entry));
    }
    var merged = merge(current.allowedToolGrants(), requestedGrants);
    if (merged.equals(current.allowedToolGrants())) {
      return new ToolBindingRequestResult(BindingStatus.NO_OP, null, "all requested stable tool ids already granted", traceId("tool-binding-noop", current.boundaryId(), request.correlationId()));
    }
    var proposal = runtimeService.proposeBehaviorChange(new AgentRuntimeService.BehaviorChangeRequest(
        request.tenantId(),
        request.agentDefinitionId(),
        request.actor(),
        BehaviorChangeProposal.TargetArtifact.TOOL_BOUNDARY,
        null,
        merged,
        "Tenant-managed stable-tool-id binding request; toolIds=" + request.stableToolIds() + "; rationale=" + request.rationale(),
        request.correlationId()));
    var status = proposal.status() == BehaviorChangeProposal.Status.DENIED ? BindingStatus.DENIED : BindingStatus.APPROVAL_REQUIRED;
    return new ToolBindingRequestResult(status, proposal, proposal.reviewReason(), traceId("tool-binding-proposal", proposal.proposalId(), request.correlationId()));
  }

  private void require(AuthContext actor, String tenantId, String capabilityId) {
    authContextResolver.requireTenant(actor, tenantId);
    authContextResolver.requireCapability(actor, capabilityId);
  }

  private AgentDefinition agent(String tenantId, String agentDefinitionId) {
    var agent = repository.agentDefinition(tenantId, agentDefinitionId).orElseThrow(() -> new AuthorizationException(404, "agent-not-found"));
    if (agent.status() == AgentLifecycleStatus.DISABLED || agent.status() == AgentLifecycleStatus.ARCHIVED) {
      throw new AuthorizationException(403, "agent-not-active");
    }
    return agent;
  }

  private MarketplacePrompt marketplacePrompt(String promptId) {
    return marketplacePrompts.stream()
        .filter(prompt -> prompt.marketplacePromptId().equals(promptId))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "marketplace-prompt-not-found"));
  }

  private ToolPermissionBoundary.ToolGrant grantFrom(ToolCatalogEntry entry) {
    return new ToolPermissionBoundary.ToolGrant(
        entry.toolId(),
        entry.category(),
        entry.capabilityId(),
        List.of("read"),
        List.of("runtime", "test"),
        entry.sideEffectLevel().name().toLowerCase(),
        "proposal_only",
        false,
        "full_work_trace");
  }

  private List<ToolPermissionBoundary.ToolGrant> merge(List<ToolPermissionBoundary.ToolGrant> existing, List<ToolPermissionBoundary.ToolGrant> additions) {
    var ordered = new LinkedHashMap<String, ToolPermissionBoundary.ToolGrant>();
    for (var grant : existing) ordered.put(grant.toolId(), grant);
    for (var grant : additions) ordered.putIfAbsent(grant.toolId(), grant);
    return List.copyOf(ordered.values());
  }

  private String traceId(String kind, String id, String correlationId) {
    return "trace-" + kind + "-" + Math.abs((String.valueOf(id) + ":" + String.valueOf(correlationId)).hashCode());
  }

  private static String checksum(String value) {
    try {
      var digest = MessageDigest.getInstance("SHA-256");
      return HexFormat.of().formatHex(digest.digest(String.valueOf(value).getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException failure) {
      throw new IllegalStateException(failure);
    }
  }

  public record MarketplacePrompt(
      String marketplacePromptId,
      String title,
      String publisher,
      String provenanceRef,
      String summary,
      String promptBody,
      List<String> tags) {
    public MarketplacePrompt {
      tags = List.copyOf(tags == null ? List.of() : tags);
      if (marketplacePromptId == null || marketplacePromptId.isBlank()) throw new IllegalArgumentException("marketplacePromptId is required");
      if (promptBody == null || promptBody.isBlank()) throw new IllegalArgumentException("promptBody is required");
    }

    public String contentChecksum() {
      return checksum(promptBody);
    }

    public Map<String, Object> browserSafe() {
      var map = new LinkedHashMap<String, Object>();
      map.put("marketplacePromptId", marketplacePromptId);
      map.put("title", title);
      map.put("publisher", publisher);
      map.put("provenanceRef", provenanceRef);
      map.put("summary", summary);
      map.put("contentChecksum", contentChecksum());
      map.put("tags", tags);
      map.put("rawPromptBodyVisible", false);
      return Map.copyOf(map);
    }
  }

  public record MarketplaceCatalogResponse(String surfaceContract, List<Map<String, Object>> entries, String traceId) {}
  public record MarketplacePromptImportRequest(String tenantId, String agentDefinitionId, AuthContext actor, String marketplacePromptId, String expectedChecksum, String rationale, String correlationId) {}
  public enum ImportStatus { PROPOSED, DENIED }
  public record MarketplaceImportResult(ImportStatus status, BehaviorChangeProposal proposal, String safeReason, String traceId) {}
  public record TenantToolBindingRequest(String tenantId, String agentDefinitionId, AuthContext actor, List<String> stableToolIds, String rationale, String correlationId) {
    public TenantToolBindingRequest {
      stableToolIds = List.copyOf(stableToolIds == null ? List.of() : stableToolIds);
    }
  }
  public enum BindingStatus { APPROVAL_REQUIRED, DENIED, NO_OP }
  public record ToolBindingRequestResult(BindingStatus status, BehaviorChangeProposal proposal, String safeReason, String traceId) {
    static ToolBindingRequestResult denied(String reason, String traceId) {
      return new ToolBindingRequestResult(BindingStatus.DENIED, null, reason, traceId);
    }
  }
}
