package ai.first.domain.agentfoundation;

/** Minimal governed prompt document pointer for reference-only runtime resolution. */
public record ReferencePromptDocument(
    String tenantId,
    String promptDocumentId,
    String displayName,
    String activePromptVersionId,
    boolean active) {}
