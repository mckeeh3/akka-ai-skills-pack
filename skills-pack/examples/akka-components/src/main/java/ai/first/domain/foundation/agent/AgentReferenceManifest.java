package ai.first.domain.foundation.agent;

import java.time.Instant;
import java.util.List;

/** Compact per-agent reference assignment rendered into prompts without full reference text. */
public record AgentReferenceManifest(
    String tenantId,
    String manifestId,
    String agentDefinitionId,
    String workstreamExpertBundleId,
    AgentLifecycleStatus status,
    int manifestVersion,
    List<Entry> entries,
    String compactManifestChecksum,
    SeedProvenance seedProvenance,
    Instant createdAt,
    Instant updatedAt) {
  public AgentReferenceManifest {
    entries = List.copyOf(entries == null ? List.of() : entries);
  }

  public record Entry(
      String stableReferenceId,
      String referenceDocumentId,
      int pinnedVersion,
      String title,
      String summary,
      String whenToConsult,
      String allowedUse,
      String accessLevel) {}
}
