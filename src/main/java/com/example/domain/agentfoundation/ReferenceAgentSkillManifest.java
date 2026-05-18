package com.example.domain.agentfoundation;

import java.util.Map;
import java.util.Set;

/** Compact skill manifest assigned to a governed AgentDefinition. */
public record ReferenceAgentSkillManifest(
    String tenantId,
    String skillManifestId,
    String skillManifestVersionId,
    String agentDefinitionId,
    Set<String> assignedSkillIds,
    Map<String, String> skillVersionRefs,
    boolean active) {

  public ReferenceAgentSkillManifest {
    assignedSkillIds = Set.copyOf(assignedSkillIds);
    skillVersionRefs = Map.copyOf(skillVersionRefs);
  }

  public boolean assignsSkill(String skillDocumentId) {
    return active && assignedSkillIds.contains(skillDocumentId);
  }

  public String activeSkillVersionFor(String skillDocumentId) {
    return skillVersionRefs.get(skillDocumentId);
  }
}
