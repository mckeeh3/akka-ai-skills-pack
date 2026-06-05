package ai.first.domain.agentfoundation;

/** Immutable skill version snapshot loaded only after readSkill authorization. */
public record ReferenceSkillVersion(
    String tenantId,
    String skillDocumentId,
    String skillVersionId,
    VersionStatus status,
    String content,
    String checksum) {

  public enum VersionStatus {
    DRAFT,
    ACTIVE,
    ARCHIVED
  }

  public boolean activeForRuntime() {
    return status == VersionStatus.ACTIVE;
  }
}
