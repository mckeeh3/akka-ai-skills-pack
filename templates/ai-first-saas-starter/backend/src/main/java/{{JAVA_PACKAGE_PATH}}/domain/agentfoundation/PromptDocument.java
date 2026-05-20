package {{JAVA_BASE_PACKAGE}}.domain.agentfoundation;

import java.time.Instant;

/** Governed prompt document plus active immutable version snapshot for the starter slice. */
public record PromptDocument(
    String tenantId,
    String promptDocumentId,
    String agentDefinitionId,
    String title,
    String promptType,
    AgentLifecycleStatus status,
    int activeVersion,
    String contentBody,
    String contentChecksum,
    String changeSummary,
    SeedProvenance seedProvenance,
    Instant createdAt,
    Instant updatedAt) {}
