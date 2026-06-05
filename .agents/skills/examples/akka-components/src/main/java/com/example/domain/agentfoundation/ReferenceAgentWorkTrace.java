package com.example.domain.agentfoundation;

/** Minimal invocation-level work trace for correlating prompt, skill, and result facts. */
public record ReferenceAgentWorkTrace(
    String tenantId,
    String agentDefinitionId,
    String correlationId,
    String promptAssemblyTraceId,
    String lastSkillLoadTraceId,
    String mode,
    boolean allowed,
    String summary) {}
