package ai.first.domain.agentfoundation;

import java.time.Instant;
import java.util.List;

/** Compact per-agent skill assignment rendered into prompts without full skill text. */
public record AgentSkillManifest(
    String tenantId,
    String manifestId,
    String agentDefinitionId,
    AgentLifecycleStatus status,
    int manifestVersion,
    List<Entry> entries,
    String compactManifestChecksum,
    SeedProvenance seedProvenance,
    Instant createdAt,
    Instant updatedAt) {
  public AgentSkillManifest {
    entries = List.copyOf(entries == null ? List.of() : entries);
  }

  public record Entry(
      String stableSkillId,
      String skillDocumentId,
      int pinnedVersion,
      String title,
      String purpose,
      String whenToUse) {}
}
