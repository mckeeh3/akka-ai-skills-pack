package com.example.domain.agentfoundation;

/** Trace fact emitted for every allowed or denied governed prompt assembly attempt. */
public record ReferencePromptAssemblyTrace(
    String tenantId,
    String agentDefinitionId,
    String promptDocumentId,
    String promptVersionId,
    String skillManifestId,
    String toolBoundaryId,
    String mode,
    String correlationId,
    boolean allowed,
    String decisionReason,
    String assembledPromptChecksum) {}
