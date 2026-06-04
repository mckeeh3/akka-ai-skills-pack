package ai.first.domain.foundation.agent;

import java.time.Instant;

/** Immutable governed prompt version snapshot used by runtime prompt assembly and history views. */
public record PromptVersion(
    String tenantId,
    String promptDocumentId,
    int version,
    String agentDefinitionId,
    String title,
    String promptType,
    AgentLifecycleStatus status,
    String contentBody,
    String contentChecksum,
    String changeSummary,
    SeedProvenance seedProvenance,
    Instant createdAt,
    Instant approvedAt,
    Instant activatedAt) {}
