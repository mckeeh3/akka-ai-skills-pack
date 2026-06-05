package com.example.application.agentfoundation;

import com.example.domain.agentfoundation.ReferenceAgentDefinition;
import com.example.domain.agentfoundation.ReferenceAgentSkillManifest;
import com.example.domain.agentfoundation.ReferenceAuthContext;
import com.example.domain.agentfoundation.ReferencePromptVersion;
import com.example.domain.agentfoundation.ReferenceSkillDocument;
import com.example.domain.agentfoundation.ReferenceSkillVersion;
import com.example.domain.agentfoundation.ReferenceToolPermissionBoundary;
import java.util.Map;
import java.util.Set;

/**
 * Main-source reference fixture factory for the optional managed-agent HTTP/test-console surface.
 * This is intentionally static sample data, not a production authorization or governance store.
 */
public final class ReferenceAgentFoundationDefaults {
  public static final String TENANT_ID = "tenant-reference-1";
  public static final String AGENT_ID = "agent-activity-guide";
  public static final String DISABLED_AGENT_ID = "agent-disabled";
  public static final String PROMPT_DOCUMENT_ID = "prompt-activity-guide";
  public static final String PROMPT_VERSION_ID = "prompt-version-active";
  public static final String SKILL_MANIFEST_ID = "manifest-activity-guide";
  public static final String ASSIGNED_SKILL_ID = "skill-rainy-day-planning";
  public static final String UNASSIGNED_SKILL_ID = "skill-premium-upsell";
  public static final String TOOL_BOUNDARY_ID = "tool-boundary-activity-guide";
  public static final String RUNTIME_MODE = "runtime";

  private ReferenceAgentFoundationDefaults() {}

  public static ReferenceAuthContext authContext(
      String tenantId, String accountId, Set<String> capabilityIds, String mode) {
    return new ReferenceAuthContext(
        tenantId,
        accountId,
        Set.of("TENANT_ADMIN"),
        capabilityIds == null ? Set.of() : Set.copyOf(capabilityIds),
        mode == null || mode.isBlank() ? RUNTIME_MODE : mode);
  }

  public static ReferenceAgentRuntimeResolver resolver(ReferenceTraceSink traceSink) {
    return new ReferenceAgentRuntimeResolver(
        agentDefinitions(),
        promptVersions(),
        manifests(),
        toolBoundaries(),
        new ReferencePromptAssembler(),
        traceSink);
  }

  public static ReferenceSkillReadAuthorizer skillReadAuthorizer(ReferenceTraceSink traceSink) {
    return new ReferenceSkillReadAuthorizer(skillDocuments(), skillVersions(), traceSink);
  }

  public static Map<String, ReferenceAgentDefinition> agentDefinitions() {
    return Map.of(AGENT_ID, activeAgent(), DISABLED_AGENT_ID, disabledAgent());
  }

  public static Map<String, ReferencePromptVersion> promptVersions() {
    return Map.of(PROMPT_VERSION_ID, activePromptVersion());
  }

  public static Map<String, ReferenceAgentSkillManifest> manifests() {
    return Map.of(SKILL_MANIFEST_ID, activeManifest());
  }

  public static Map<String, ReferenceToolPermissionBoundary> toolBoundaries() {
    return Map.of(TOOL_BOUNDARY_ID, activeToolBoundary());
  }

  public static Map<String, ReferenceSkillDocument> skillDocuments() {
    return Map.of(
        ASSIGNED_SKILL_ID,
        activeAssignedSkillDocument(),
        UNASSIGNED_SKILL_ID,
        unassignedSkillDocument());
  }

  public static Map<String, ReferenceSkillVersion> skillVersions() {
    return Map.of(
        "skill-version-active",
        activeAssignedSkillVersion(),
        "skill-version-unassigned",
        unassignedSkillVersion());
  }

  private static ReferenceAgentDefinition activeAgent() {
    return new ReferenceAgentDefinition(
        TENANT_ID,
        AGENT_ID,
        "Activity Guide",
        ReferenceAgentDefinition.LifecycleStatus.ACTIVE,
        PROMPT_DOCUMENT_ID,
        PROMPT_VERSION_ID,
        SKILL_MANIFEST_ID,
        TOOL_BOUNDARY_ID,
        "model-config-reference-small",
        "advisory");
  }

  private static ReferenceAgentDefinition disabledAgent() {
    return new ReferenceAgentDefinition(
        TENANT_ID,
        DISABLED_AGENT_ID,
        "Disabled Activity Guide",
        ReferenceAgentDefinition.LifecycleStatus.DISABLED,
        PROMPT_DOCUMENT_ID,
        PROMPT_VERSION_ID,
        SKILL_MANIFEST_ID,
        TOOL_BOUNDARY_ID,
        "model-config-reference-small",
        "advisory");
  }

  private static ReferencePromptVersion activePromptVersion() {
    return new ReferencePromptVersion(
        TENANT_ID,
        PROMPT_DOCUMENT_ID,
        PROMPT_VERSION_ID,
        ReferencePromptVersion.VersionStatus.ACTIVE,
        "You are an activity guide. Use governed skills only after readSkill authorization.",
        "prompt-checksum-active");
  }

  private static ReferenceAgentSkillManifest activeManifest() {
    return new ReferenceAgentSkillManifest(
        TENANT_ID,
        SKILL_MANIFEST_ID,
        "manifest-version-active",
        AGENT_ID,
        Set.of(ASSIGNED_SKILL_ID),
        Map.of(ASSIGNED_SKILL_ID, "skill-version-active"),
        Map.of(
            ASSIGNED_SKILL_ID,
            new ReferenceAgentSkillManifest.SkillEntry(
                ASSIGNED_SKILL_ID,
                "Rainy Day Planning",
                "Plan safe activity recommendations when weather is risky.",
                "Use when a request involves rain, storms, weather risk, or indoor alternatives.")),
        true);
  }

  private static ReferenceSkillDocument activeAssignedSkillDocument() {
    return new ReferenceSkillDocument(
        TENANT_ID, ASSIGNED_SKILL_ID, "Rainy Day Planning", "skill-version-active", true);
  }

  private static ReferenceSkillVersion activeAssignedSkillVersion() {
    return new ReferenceSkillVersion(
        TENANT_ID,
        ASSIGNED_SKILL_ID,
        "skill-version-active",
        ReferenceSkillVersion.VersionStatus.ACTIVE,
        "Recommend indoor activities first when weather risk is high.",
        "skill-checksum-active");
  }

  private static ReferenceSkillDocument unassignedSkillDocument() {
    return new ReferenceSkillDocument(
        TENANT_ID, UNASSIGNED_SKILL_ID, "Premium Upsell", "skill-version-unassigned", true);
  }

  private static ReferenceSkillVersion unassignedSkillVersion() {
    return new ReferenceSkillVersion(
        TENANT_ID,
        UNASSIGNED_SKILL_ID,
        "skill-version-unassigned",
        ReferenceSkillVersion.VersionStatus.ACTIVE,
        "This skill is intentionally not assigned to the reference agent.",
        "skill-checksum-unassigned");
  }

  private static ReferenceToolPermissionBoundary activeToolBoundary() {
    return new ReferenceToolPermissionBoundary(
        TENANT_ID,
        TOOL_BOUNDARY_ID,
        "tool-boundary-version-active",
        AGENT_ID,
        Set.of(ReferenceSkillReadAuthorizer.READ_SKILL_TOOL_ID),
        Set.of(RUNTIME_MODE, "test", "replay"),
        true);
  }
}
