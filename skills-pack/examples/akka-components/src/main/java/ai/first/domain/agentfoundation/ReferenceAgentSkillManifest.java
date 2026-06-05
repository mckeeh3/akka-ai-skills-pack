package ai.first.domain.agentfoundation;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Compact skill manifest assigned to a governed AgentDefinition. */
public record ReferenceAgentSkillManifest(
    String tenantId,
    String skillManifestId,
    String skillManifestVersionId,
    String agentDefinitionId,
    Set<String> assignedSkillIds,
    Map<String, String> skillVersionRefs,
    Map<String, SkillEntry> skillEntries,
    boolean active) {

  public ReferenceAgentSkillManifest {
    assignedSkillIds = Set.copyOf(assignedSkillIds);
    skillVersionRefs = Map.copyOf(skillVersionRefs);
    skillEntries = Map.copyOf(skillEntries);
  }

  public ReferenceAgentSkillManifest(
      String tenantId,
      String skillManifestId,
      String skillManifestVersionId,
      String agentDefinitionId,
      Set<String> assignedSkillIds,
      Map<String, String> skillVersionRefs,
      boolean active) {
    this(
        tenantId,
        skillManifestId,
        skillManifestVersionId,
        agentDefinitionId,
        assignedSkillIds,
        skillVersionRefs,
        assignedSkillIds.stream()
            .collect(
                Collectors.toMap(
                    skillId -> skillId,
                    skillId ->
                        new SkillEntry(
                            skillId,
                            skillId,
                            "Assigned governed skill",
                            "Use when the agent request matches this assigned skill."))),
        active);
  }

  public record SkillEntry(String skillId, String displayName, String purpose, String whenToUse) {}

  public boolean assignsSkill(String skillDocumentId) {
    return active && assignedSkillIds.contains(skillDocumentId);
  }

  public String activeSkillVersionFor(String skillDocumentId) {
    return skillVersionRefs.get(skillDocumentId);
  }
}
