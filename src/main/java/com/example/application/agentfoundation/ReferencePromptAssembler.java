package com.example.application.agentfoundation;

import com.example.domain.agentfoundation.ReferenceAgentDefinition;
import com.example.domain.agentfoundation.ReferenceAgentSkillManifest;
import com.example.domain.agentfoundation.ReferenceAuthContext;
import com.example.domain.agentfoundation.ReferencePromptVersion;
import com.example.domain.agentfoundation.ReferenceToolPermissionBoundary;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.stream.Collectors;

/** Deterministic prompt assembler that renders compact skill manifest entries only. */
public final class ReferencePromptAssembler {

  public String assemble(
      ReferenceAuthContext authContext,
      ReferenceAgentDefinition agentDefinition,
      ReferencePromptVersion promptVersion,
      ReferenceAgentSkillManifest skillManifest,
      ReferenceToolPermissionBoundary toolBoundary) {
    return String.join(
        "\n",
        "Platform: obey backend authorization and tenant isolation checks.",
        "Tenant: " + authContext.tenantId(),
        "AgentDefinition: "
            + agentDefinition.agentDefinitionId()
            + " authority="
            + agentDefinition.authorityLevel(),
        "PromptVersion: " + promptVersion.promptVersionId(),
        "Prompt: " + promptVersion.content(),
        "AgentSkillManifest compact: " + compactManifest(skillManifest),
        "ToolPermissionBoundary: "
            + toolBoundary.toolBoundaryId()
            + " grants="
            + toolBoundary.allowedToolIds().stream().sorted().collect(Collectors.joining(",")),
        "Rule: full skill text is available only through authorized readSkill(skillId).");
  }

  public String checksum(String text) {
    try {
      var digest = MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));
      var hex = new StringBuilder();
      for (byte b : digest) {
        hex.append(String.format("%02x", b));
      }
      return hex.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 not available", e);
    }
  }

  private String compactManifest(ReferenceAgentSkillManifest skillManifest) {
    return skillManifest.assignedSkillIds().stream()
        .sorted(Comparator.naturalOrder())
        .map(skillId -> skillId + "@" + skillManifest.activeSkillVersionFor(skillId))
        .collect(Collectors.joining(","));
  }
}
