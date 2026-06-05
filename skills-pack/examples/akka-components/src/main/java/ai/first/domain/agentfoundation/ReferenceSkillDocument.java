package ai.first.domain.agentfoundation;

/** Minimal governed skill document pointer for reference-only runtime skill loading. */
public record ReferenceSkillDocument(
    String tenantId,
    String skillDocumentId,
    String displayName,
    String activeSkillVersionId,
    boolean active) {}
