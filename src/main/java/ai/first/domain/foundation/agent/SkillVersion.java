package ai.first.domain.foundation.agent;

import java.time.Instant;
import java.util.List;

/** Immutable governed skill version snapshot loaded only through authorized readSkill flows. */
public record SkillVersion(
    String tenantId,
    String skillDocumentId,
    String stableSkillId,
    int version,
    String title,
    String purpose,
    String whenToUse,
    List<String> tags,
    AgentLifecycleStatus status,
    String contentBody,
    String contentChecksum,
    SeedProvenance seedProvenance,
    Instant createdAt,
    Instant approvedAt,
    Instant activatedAt) {
  public SkillVersion {
    tags = List.copyOf(tags == null ? List.of() : tags);
  }
}
