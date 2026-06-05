package com.example.domain.agentfoundation;

/** Trace fact emitted for every allowed or denied readSkill(skillId) attempt. */
public record ReferenceSkillLoadTrace(
    String tenantId,
    String agentDefinitionId,
    String skillManifestId,
    String requestedSkillId,
    String resolvedSkillVersionId,
    String toolBoundaryId,
    String mode,
    String correlationId,
    boolean allowed,
    String decisionReason) {}
