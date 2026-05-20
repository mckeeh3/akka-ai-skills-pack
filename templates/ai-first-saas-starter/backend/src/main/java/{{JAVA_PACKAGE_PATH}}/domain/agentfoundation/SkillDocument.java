package {{JAVA_BASE_PACKAGE}}.domain.agentfoundation;

import java.time.Instant;
import java.util.List;

/** Governed runtime skill document plus active immutable version snapshot for the starter slice. */
public record SkillDocument(
    String tenantId,
    String skillDocumentId,
    String stableSkillId,
    String title,
    String purpose,
    String whenToUse,
    List<String> tags,
    AgentLifecycleStatus status,
    int activeVersion,
    String contentBody,
    String contentChecksum,
    SeedProvenance seedProvenance,
    Instant createdAt,
    Instant updatedAt) {
  public SkillDocument {
    tags = List.copyOf(tags == null ? List.of() : tags);
  }
}
