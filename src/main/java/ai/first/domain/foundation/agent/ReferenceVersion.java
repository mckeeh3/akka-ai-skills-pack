package ai.first.domain.foundation.agent;

import java.time.Instant;
import java.util.List;

/** Immutable governed reference version snapshot loaded only through authorized readReferenceDoc flows. */
public record ReferenceVersion(
    String tenantId,
    String referenceDocumentId,
    String stableReferenceId,
    int version,
    String title,
    String summary,
    String whenToConsult,
    ReferenceDocument.ReferenceType referenceType,
    String accessLevel,
    List<String> tags,
    AgentLifecycleStatus status,
    String contentBody,
    String contentChecksum,
    SeedProvenance seedProvenance,
    Instant createdAt,
    Instant approvedAt,
    Instant activatedAt) {
  public ReferenceVersion {
    tags = List.copyOf(tags == null ? List.of() : tags);
  }
}
