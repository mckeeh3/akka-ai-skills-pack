package ai.first.domain.agentfoundation;

import java.time.Instant;
import java.util.List;

/** Governed runtime reference document plus active immutable version snapshot for the starter slice. */
public record ReferenceDocument(
    String tenantId,
    String referenceDocumentId,
    String stableReferenceId,
    String title,
    String summary,
    String whenToConsult,
    ReferenceType referenceType,
    String accessLevel,
    List<String> tags,
    AgentLifecycleStatus status,
    int activeVersion,
    String contentBody,
    String contentChecksum,
    SeedProvenance seedProvenance,
    Instant createdAt,
    Instant updatedAt) {
  public ReferenceDocument {
    tags = List.copyOf(tags == null ? List.of() : tags);
  }

  public enum ReferenceType {
    POLICY,
    PROCESS,
    DOMAIN_RULE,
    CHECKLIST,
    PRODUCT_CONFIG,
    COMPLIANCE,
    CUSTOMER_PROCEDURE,
    OTHER
  }
}
