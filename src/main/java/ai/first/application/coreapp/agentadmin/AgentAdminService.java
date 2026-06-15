package ai.first.application.coreapp.agentadmin;

import ai.first.domain.foundation.agent.ModelConfigRef;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.AgentReferenceManifest;
import ai.first.domain.foundation.agent.AgentSkillManifest;
import ai.first.domain.foundation.agent.PromptDocument;
import ai.first.domain.foundation.agent.ReferenceDocument;
import ai.first.domain.foundation.agent.SeedProvenance;
import ai.first.domain.foundation.agent.SkillDocument;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.ScopeType;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import ai.first.application.foundation.agent.AgentBehaviorRepository;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;

/** Deterministic Agent Admin read facade for browser-safe, tenant-scoped managed-agent artifacts. */
public final class AgentAdminService {
  public static final String LIST_DEFINITIONS = "agent_admin.list_definitions";
  public static final String GET_DEFINITION = "agent_admin.get_definition";
  public static final String GET_PROMPT_VERSION = "agent_admin.get_prompt_version";
  public static final String GET_SKILL_VERSION = "agent_admin.get_skill_version";
  public static final String GET_REFERENCE_VERSION = "agent_admin.get_reference_version";
  public static final String GET_MANIFEST = "agent_admin.get_manifest";
  public static final String GET_MODEL_REF = "agent_admin.get_model_ref";
  public static final String GET_TOOL_BOUNDARY = "agent_admin.get_tool_boundary";
  public static final String LIST_SEED_MATERIAL = "agent_admin.list_seed_material";

  private static final int PREVIEW_CHARS = 220;

  private final AgentBehaviorRepository repository;
  private final AuthContextResolver authContextResolver;

  public AgentAdminService(AgentBehaviorRepository repository, AuthContextResolver authContextResolver) {
    this.repository = Objects.requireNonNull(repository);
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
  }

  public Map<String, Object> catalog(AuthContextResolver.ResolvedMe actor, String correlationId) {
    require(actor, LIST_DEFINITIONS, correlationId, "agent_admin.catalog.v1");
    var rows = repository.agentDefinitions(actor.selectedContext().tenantId()).stream()
        .map(agent -> mapOf(
            "id", agent.agentDefinitionId(),
            "displayName", agent.displayName(),
            "status", agent.status().name().toLowerCase(),
            "authorityLevel", agent.authorityLevel().name(),
            "placement", agent.placement().name(),
            "functionalAreaId", agent.functionalAreaId(),
            "modelConfigRefId", agent.modelConfigRefId(),
            "providerReadiness", providerReadiness(actor, agent),
            "seedStatus", seedStatus(agent.seedProvenance()),
            "tracePolicy", agent.traceRequirements(),
            "traceId", traceId("definition", agent.agentDefinitionId(), correlationId)))
        .toList();
    return mapOf(
        "surfaceContract", "agent_admin.catalog.v1",
        "surfaceContractAliases", List.of("surface.agent_admin.catalog.v1"),
        "query", "tenant:" + actor.selectedContext().tenantId(),
        "rows", rows,
        "pageInfo", mapOf("totalKnownCount", rows.size()),
        "capabilityIds", List.of(LIST_DEFINITIONS, GET_DEFINITION, GET_PROMPT_VERSION, GET_SKILL_VERSION, GET_REFERENCE_VERSION, GET_MANIFEST, GET_MODEL_REF, GET_TOOL_BOUNDARY, LIST_SEED_MATERIAL),
        "providerReadiness", providerReadinessSummary(actor),
        "seedMaterial", seedMaterial(actor, correlationId),
        "redaction", redactionMetadata(),
        "traceLinks", List.of(traceId("catalog", actor.selectedContext().tenantId(), correlationId)),
        "emptyCopy", "Empty when no governed AgentDefinition records are seeded.");
  }

  public Map<String, Object> definitionDetail(AuthContextResolver.ResolvedMe actor, String agentDefinitionId, String correlationId) {
    require(actor, GET_DEFINITION, correlationId, "agent_admin.definition.v1");
    var agent = agent(actor, firstNonBlank(agentDefinitionId, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID));
    var prompt = repository.promptDocument(actor.selectedContext().tenantId(), agent.promptDocumentId()).orElse(null);
    var skillManifest = repository.skillManifest(actor.selectedContext().tenantId(), agent.skillManifestId()).orElse(null);
    var referenceManifest = repository.referenceManifest(actor.selectedContext().tenantId(), agent.referenceManifestId()).orElse(null);
    var boundary = repository.toolBoundary(actor.selectedContext().tenantId(), agent.toolBoundaryId()).orElse(null);
    var model = repository.modelConfigRef(actor.selectedContext().tenantId(), agent.modelConfigRefId()).orElse(null);
    return mapOf(
        "surfaceContract", "agent_admin.definition.v1",
        "surfaceContractAliases", List.of("surface.agent_admin.definition_detail.v1"),
        "recordId", agent.agentDefinitionId(),
        "recordLabel", agent.displayName(),
        "recordKind", "AgentDefinition",
        "summary", "Backend-authoritative AgentDefinition detail; behavior changes must use deterministic proposal/review/activation commands.",
        "fields", List.of(
            field("status", "Status", agent.status().name().toLowerCase(), false, null),
            field("authorityLevel", "Authority tier", agent.authorityLevel().name(), false, null),
            field("promptDocumentId", "Prompt", agent.promptDocumentId() + "@" + agent.activePromptVersion(), false, null),
            field("skillManifestId", "Skill manifest", agent.skillManifestId() + "@" + agent.activeSkillManifestVersion(), false, null),
            field("referenceManifestId", "Reference manifest", agent.referenceManifestId() + "@" + agent.activeReferenceManifestVersion(), false, null),
            field("toolBoundaryId", "Tool boundary", agent.toolBoundaryId() + "@" + agent.activeToolBoundaryVersion(), false, null),
            field("modelConfigRef", "Model ref", agent.modelConfigRefId(), false, "Provider credential values are never browser-visible")),
        "relatedArtifacts", List.of(
            artifactRef("prompt", agent.promptDocumentId(), prompt == null ? null : prompt.status().name(), GET_PROMPT_VERSION),
            artifactRef("skill_manifest", agent.skillManifestId(), skillManifest == null ? null : skillManifest.status().name(), GET_MANIFEST),
            artifactRef("reference_manifest", agent.referenceManifestId(), referenceManifest == null ? null : referenceManifest.status().name(), GET_MANIFEST),
            artifactRef("tool_boundary", agent.toolBoundaryId(), boundary == null ? null : boundary.status().name(), GET_TOOL_BOUNDARY),
            artifactRef("model_ref", agent.modelConfigRefId(), model == null ? null : model.status().name(), GET_MODEL_REF)),
        "providerReadiness", providerReadiness(actor, agent),
        "seedStatus", seedStatus(agent.seedProvenance()),
        "permissionState", mapOf("canEdit", false, "reason", "Use inert behavior-change proposals; no direct mutation from read surfaces.", "authoritativeCapabilityId", GET_DEFINITION),
        "audit", mapOf("lastEventType", "AgentDefinitionDetailDisplayed", "traceIds", List.of(traceId("definition", agent.agentDefinitionId(), correlationId))),
        "redaction", redactionMetadata(),
        "noDirectMutation", true);
  }

  public Map<String, Object> promptDetail(AuthContextResolver.ResolvedMe actor, String agentDefinitionId, String correlationId) {
    require(actor, GET_PROMPT_VERSION, correlationId, "agent_admin.prompt_version.v1");
    var agent = agent(actor, firstNonBlank(agentDefinitionId, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID));
    var prompt = repository.promptDocument(actor.selectedContext().tenantId(), agent.promptDocumentId()).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    return mapOf(
        "surfaceContract", "agent_admin.prompt_version.v1",
        "surfaceContractAliases", List.of("surface.agent_admin.prompt_versions.v1"),
        "capabilityAliases", List.of("agent.prompts.govern"),
        "recordId", prompt.promptDocumentId(),
        "recordLabel", prompt.title() + "@" + prompt.activeVersion(),
        "recordKind", "PromptDocument",
        "status", prompt.status().name().toLowerCase(),
        "promptType", prompt.promptType(),
        "redactedPreview", preview(prompt.contentBody()),
        "fullContentAvailableInBrowser", false,
        "checksum", prompt.contentChecksum(),
        "changeSummary", prompt.changeSummary(),
        "seedStatus", seedStatus(prompt.seedProvenance()),
        "traceLinks", List.of(traceId("prompt", prompt.promptDocumentId(), correlationId)),
        "redaction", redactionMetadata(),
        "noDirectMutation", true);
  }

  public Map<String, Object> skillDetail(AuthContextResolver.ResolvedMe actor, String agentDefinitionId, String stableSkillId, String correlationId) {
    require(actor, GET_SKILL_VERSION, correlationId, "agent_admin.skill_version.v1");
    var agent = agent(actor, firstNonBlank(agentDefinitionId, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID));
    var manifest = repository.skillManifest(actor.selectedContext().tenantId(), agent.skillManifestId()).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    var entry = manifest.entries().stream()
        .filter(candidate -> stableSkillId == null || stableSkillId.isBlank() || candidate.stableSkillId().equals(stableSkillId))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    var skill = repository.skillDocument(actor.selectedContext().tenantId(), entry.skillDocumentId()).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    return mapOf(
        "surfaceContract", "agent_admin.skill_version.v1",
        "surfaceContractAliases", List.of("surface.agent_admin.skill_versions.v1"),
        "capabilityAliases", List.of("agent.skills.govern", "agent.read_skill"),
        "recordId", skill.skillDocumentId(),
        "stableSkillId", skill.stableSkillId(),
        "recordLabel", skill.title() + "@" + skill.activeVersion(),
        "recordKind", "SkillDocument",
        "status", skill.status().name().toLowerCase(),
        "pinnedManifestVersion", entry.pinnedVersion(),
        "purpose", skill.purpose(),
        "whenToUse", skill.whenToUse(),
        "redactedPreview", preview(skill.contentBody()),
        "fullContentAvailableInBrowser", false,
        "checksum", skill.contentChecksum(),
        "seedStatus", seedStatus(skill.seedProvenance()),
        "readSkillRuntime", mapOf("toolId", "readSkill", "requiredManifestId", manifest.manifestId(), "assignedOnly", true, "traceType", "SkillLoadTrace"),
        "traceLinks", List.of(traceId("skill-version", skill.stableSkillId(), correlationId)),
        "redaction", redactionMetadata(),
        "noDirectMutation", true);
  }

  public Map<String, Object> manifestDetail(AuthContextResolver.ResolvedMe actor, String agentDefinitionId, String correlationId) {
    require(actor, GET_MANIFEST, correlationId, "agent_admin.manifest.v1");
    var agent = agent(actor, firstNonBlank(agentDefinitionId, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID));
    var skills = repository.skillManifest(actor.selectedContext().tenantId(), agent.skillManifestId()).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    var references = repository.referenceManifest(actor.selectedContext().tenantId(), agent.referenceManifestId()).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    return mapOf(
        "surfaceContract", "agent_admin.manifest.v1",
        "surfaceContractAliases", List.of("surface.agent_admin.manifest_detail.v1"),
        "capabilityAliases", List.of("agent.manifests.manage", "agent.skills.govern"),
        "recordId", skills.manifestId() + ":" + references.manifestId(),
        "recordKind", "AgentSkillManifest+AgentReferenceManifest",
        "skillManifest", mapOf("manifestId", skills.manifestId(), "status", skills.status().name().toLowerCase(), "version", skills.manifestVersion(), "checksum", skills.compactManifestChecksum(), "entries", skillEntries(actor, skills), "seedStatus", seedStatus(skills.seedProvenance())),
        "referenceManifest", mapOf("manifestId", references.manifestId(), "status", references.status().name().toLowerCase(), "version", references.manifestVersion(), "checksum", references.compactManifestChecksum(), "entries", referenceEntries(actor, references), "seedStatus", seedStatus(references.seedProvenance())),
        "traceLinks", List.of(traceId("manifest", skills.manifestId(), correlationId), traceId("manifest", references.manifestId(), correlationId)),
        "redaction", redactionMetadata(),
        "noDirectMutation", true);
  }

  public Map<String, Object> toolBoundaryDetail(AuthContextResolver.ResolvedMe actor, String agentDefinitionId, String correlationId) {
    require(actor, GET_TOOL_BOUNDARY, correlationId, "agent_admin.tool_boundary.v1");
    var agent = agent(actor, firstNonBlank(agentDefinitionId, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID));
    var boundary = repository.toolBoundary(actor.selectedContext().tenantId(), agent.toolBoundaryId()).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    return mapOf(
        "surfaceContract", "agent_admin.tool_boundary.v1",
        "surfaceContractAliases", List.of("surface.agent_admin.tool_boundary.v1"),
        "capabilityAliases", List.of("agent.tool_boundaries.manage"),
        "recordId", boundary.boundaryId(),
        "recordKind", "ToolPermissionBoundary",
        "status", boundary.status().name().toLowerCase(),
        "version", boundary.boundaryVersion(),
        "grants", boundary.allowedToolGrants().stream().map(this::toolGrant).toList(),
        "checksum", boundary.checksum(),
        "seedStatus", seedStatus(boundary.seedProvenance()),
        "traceLinks", List.of(traceId("tool-boundary", boundary.boundaryId(), correlationId)),
        "redaction", redactionMetadata(),
        "noDirectMutation", true);
  }

  public Map<String, Object> modelRefDetail(AuthContextResolver.ResolvedMe actor, String agentDefinitionId, String correlationId) {
    require(actor, GET_MODEL_REF, correlationId, "agent_admin.model_ref.v1");
    var agent = agent(actor, firstNonBlank(agentDefinitionId, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID));
    var model = repository.modelConfigRef(actor.selectedContext().tenantId(), agent.modelConfigRefId()).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    return mapOf(
        "surfaceContract", "agent_admin.model_ref.v1",
        "surfaceContractAliases", List.of("surface.agent_admin.model_ref.v1"),
        "recordId", model.modelConfigRefId(),
        "recordKind", "ModelConfigRef",
        "displayName", model.displayName(),
        "providerAlias", safe(model.providerAlias()),
        "providerCredential", "[REDACTED]",
        "status", model.status().name().toLowerCase(),
        "allowedModes", model.allowedModes(),
        "allowedCapabilityIds", model.allowedCapabilityIds(),
        "allowedAuthorityLevels", model.allowedAuthorityLevels().stream().map(Enum::name).toList(),
        "seedStatus", seedStatus(model.seedProvenance()),
        "readiness", providerReadiness(actor, agent),
        "traceLinks", List.of(traceId("model-ref", model.modelConfigRefId(), correlationId)),
        "redaction", redactionMetadata(),
        "noDirectMutation", true);
  }

  public Map<String, Object> seedMaterialDetail(AuthContextResolver.ResolvedMe actor, String correlationId) {
    require(actor, LIST_SEED_MATERIAL, correlationId, "agent_admin.seed_material.v1");
    return mapOf(
        "surfaceContract", "agent_admin.seed_material.v1",
        "surfaceContractAliases", List.of("surface.agent_admin.seed_import.v1"),
        "rows", seedMaterial(actor, correlationId),
        "traceLinks", List.of(traceId("seed", actor.selectedContext().tenantId(), correlationId)),
        "redaction", redactionMetadata(),
        "noDirectMutation", true);
  }

  private void require(AuthContextResolver.ResolvedMe actor, String capabilityId, String correlationId, String surfaceContract) {
    requireTenantOrganizationAdmin(actor);
    authContextResolver.requireTenant(actor.selectedContext(), actor.selectedContext().tenantId());
    authContextResolver.requireCapability(actor.selectedContext(), capabilityId);
    authContextResolver.appendProtectedReadTrace(actor, capabilityId, surfaceContract, correlationId);
  }

  private void requireTenantOrganizationAdmin(AuthContextResolver.ResolvedMe actor) {
    if (actor.selectedContext().scopeType() != ScopeType.TENANT || !actor.selectedContext().roles().contains(FoundationRole.TENANT_ADMIN)) {
      throw new AuthorizationException(403, "agent-admin-requires-tenant-admin");
    }
  }

  private AgentDefinition agent(AuthContextResolver.ResolvedMe actor, String agentDefinitionId) {
    return repository.agentDefinition(actor.selectedContext().tenantId(), agentDefinitionId).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
  }

  private List<Map<String, Object>> skillEntries(AuthContextResolver.ResolvedMe actor, AgentSkillManifest manifest) {
    return manifest.entries().stream().map(entry -> {
      var skill = repository.skillDocument(actor.selectedContext().tenantId(), entry.skillDocumentId()).orElse(null);
      return mapOf("stableSkillId", entry.stableSkillId(), "skillDocumentId", entry.skillDocumentId(), "pinnedVersion", entry.pinnedVersion(), "title", entry.title(), "purpose", entry.purpose(), "whenToUse", entry.whenToUse(), "status", skill == null ? "missing" : skill.status().name().toLowerCase(), "redactedPreview", skill == null ? null : preview(skill.contentBody()), "fullContentAvailableInBrowser", false, "seedStatus", skill == null ? null : seedStatus(skill.seedProvenance()), "traceId", traceId("skill", entry.stableSkillId(), manifest.manifestId()));
    }).toList();
  }

  private List<Map<String, Object>> referenceEntries(AuthContextResolver.ResolvedMe actor, AgentReferenceManifest manifest) {
    return manifest.entries().stream().map(entry -> {
      var reference = repository.referenceDocument(actor.selectedContext().tenantId(), entry.referenceDocumentId()).orElse(null);
      return mapOf("stableReferenceId", entry.stableReferenceId(), "referenceDocumentId", entry.referenceDocumentId(), "pinnedVersion", entry.pinnedVersion(), "title", entry.title(), "summary", entry.summary(), "whenToConsult", entry.whenToConsult(), "allowedUse", entry.allowedUse(), "accessLevel", entry.accessLevel(), "status", reference == null ? "missing" : reference.status().name().toLowerCase(), "redactedPreview", reference == null ? null : preview(reference.contentBody()), "fullContentAvailableInBrowser", false, "seedStatus", reference == null ? null : seedStatus(reference.seedProvenance()), "traceId", traceId("reference", entry.stableReferenceId(), manifest.manifestId()));
    }).toList();
  }

  private List<Map<String, Object>> seedMaterial(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var tenantId = actor.selectedContext().tenantId();
    var rows = new java.util.ArrayList<Map<String, Object>>();
    repository.agentDefinitions(tenantId).forEach(agent -> rows.add(seedRow("AgentDefinition", agent.agentDefinitionId(), agent.status(), agent.seedProvenance(), correlationId)));
    repository.skillDocuments(tenantId).forEach(skill -> rows.add(seedRow("SkillDocument", skill.skillDocumentId(), skill.status(), skill.seedProvenance(), correlationId)));
    repository.referenceDocuments(tenantId).forEach(reference -> rows.add(seedRow("ReferenceDocument", reference.referenceDocumentId(), reference.status(), reference.seedProvenance(), correlationId)));
    return List.copyOf(rows);
  }

  private Map<String, Object> seedRow(String kind, String id, AgentLifecycleStatus status, SeedProvenance provenance, String correlationId) {
    return mapOf("artifactKind", kind, "artifactId", id, "status", status.name().toLowerCase(), "seedStatus", seedStatus(provenance), "traceId", traceId("seed", id, correlationId));
  }

  private Map<String, Object> providerReadinessSummary(AuthContextResolver.ResolvedMe actor) {
    var total = repository.agentDefinitions(actor.selectedContext().tenantId()).size();
    var ready = repository.agentDefinitions(actor.selectedContext().tenantId()).stream().filter(agent -> "ready".equals(providerReadiness(actor, agent).get("status"))).count();
    return mapOf("status", ready == total ? "ready" : "warning", "readyAgents", ready, "totalAgents", total, "secretVisibility", "redacted");
  }

  private Map<String, Object> providerReadiness(AuthContextResolver.ResolvedMe actor, AgentDefinition agent) {
    var model = repository.modelConfigRef(actor.selectedContext().tenantId(), agent.modelConfigRefId());
    if (model.isEmpty()) return mapOf("status", "blocked_provider_or_runtime", "safeReason", "ModelConfigRef is unavailable.", "providerAlias", null, "secretVisibility", "redacted");
    var value = model.orElseThrow();
    if (value.status() != AgentLifecycleStatus.ACTIVE) return mapOf("status", "blocked_provider_or_runtime", "safeReason", "ModelConfigRef is not active.", "providerAlias", safe(value.providerAlias()), "secretVisibility", "redacted");
    return mapOf("status", "ready", "safeReason", "Provider alias is configured; credentials remain backend-only.", "providerAlias", safe(value.providerAlias()), "secretVisibility", "redacted");
  }

  private Map<String, Object> seedStatus(SeedProvenance provenance) {
    if (provenance == null) return mapOf("source", "unknown", "tenantCustomized", false, "checksum", null);
    return mapOf("seedBundleId", provenance.seedBundleId(), "contentVersion", provenance.contentVersion(), "resourceId", provenance.resourceId(), "checksum", provenance.checksum(), "importedAt", provenance.importedAt() == null ? null : provenance.importedAt().toString(), "importerActor", provenance.importerActor(), "tenantCustomized", provenance.tenantCustomized());
  }

  private Map<String, Object> redactionMetadata() {
    return mapOf("browserSafe", true, "omittedFieldKeys", List.of("rawPromptBody", "rawSkillBody", "rawReferenceBody", "rawProviderCredential", "providerCredentialValue", "rawJwt"), "previewLimitChars", PREVIEW_CHARS);
  }

  private Map<String, Object> toolGrant(ToolPermissionBoundary.ToolGrant grant) {
    return mapOf("toolId", grant.toolId(), "category", grant.category().name(), "capabilityId", grant.capabilityId(), "allowedOperations", grant.allowedOperations(), "allowedModes", grant.allowedModes(), "sideEffectLevel", grant.sideEffectLevel(), "autonomy", grant.autonomy(), "idempotencyRequired", grant.idempotencyRequired(), "traceLevel", grant.traceLevel());
  }

  private Map<String, Object> field(String id, String label, Object value, boolean editable, String disabledReason) {
    return mapOf("fieldId", id, "label", label, "value", value, "editable", editable, "disabledReason", disabledReason);
  }

  private Map<String, Object> artifactRef(String kind, String id, String status, String capabilityId) {
    return mapOf("artifactKind", kind, "artifactId", id, "status", status == null ? "missing" : status.toLowerCase(), "capabilityId", capabilityId);
  }

  private String preview(String content) {
    var redacted = safe(content == null ? "" : content).replaceAll("\\s+", " ").trim();
    return redacted.length() <= PREVIEW_CHARS ? redacted : redacted.substring(0, PREVIEW_CHARS) + "…";
  }

  private String safe(String value) {
    return value == null ? null : value.replaceAll("(?i)(api[_-]?key|secret|token)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
  }

  private String traceId(String kind, String id, String correlationId) {
    return "trace-agent-admin-" + kind + "-" + Math.abs((String.valueOf(id) + ":" + String.valueOf(correlationId)).hashCode());
  }

  private String firstNonBlank(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }

  private Map<String, Object> mapOf(Object... entries) {
    var map = new LinkedHashMap<String, Object>();
    for (var index = 0; index < entries.length; index += 2) map.put(String.valueOf(entries[index]), entries[index + 1]);
    return map;
  }
}
