package com.example.domain.agentfoundation;

/** Immutable prompt version snapshot used by governed-agent reference tests. */
public record ReferencePromptVersion(
    String tenantId,
    String promptDocumentId,
    String promptVersionId,
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
