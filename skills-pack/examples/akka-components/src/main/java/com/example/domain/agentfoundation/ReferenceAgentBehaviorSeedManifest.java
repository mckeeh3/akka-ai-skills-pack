package com.example.domain.agentfoundation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Small classpath-backed manifest for implementation-developed default agent behavior documents.
 *
 * <p>This is a reference shape for seed resources packaged with the app artifact and imported into
 * governed app state during install or tenant bootstrap.
 */
public record ReferenceAgentBehaviorSeedManifest(
    String seedBundleId,
    String appVersion,
    String contentVersion,
    String agentDefinitionId,
    String agentDisplayName,
    String modelConfigRef,
    String authorityLevel,
    String promptDocumentId,
    String promptVersionId,
    String promptResource,
    String promptChecksum,
    String previousPromptChecksum,
    String skillManifestId,
    String skillManifestVersionId,
    String toolBoundaryId,
    String toolBoundaryVersionId,
    List<SeedSkill> skills) {

  public ReferenceAgentBehaviorSeedManifest {
    skills = List.copyOf(skills);
  }

  public record SeedSkill(
      String skillDocumentId,
      String skillVersionId,
      String displayName,
      String purpose,
      String whenToUse,
      String resource,
      String checksum) {}

  public static ReferenceAgentBehaviorSeedManifest fromProperties(InputStream inputStream) {
    var properties = new Properties();
    try (inputStream) {
      properties.load(inputStream);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to load seed manifest", e);
    }

    var skillIds = Arrays.stream(required(properties, "skills").split(","))
        .map(String::trim)
        .filter(value -> !value.isBlank())
        .toList();
    var skills = skillIds.stream()
        .map(
            skillId ->
                new SeedSkill(
                    skillId,
                    required(properties, "skill." + skillId + ".versionId"),
                    required(properties, "skill." + skillId + ".displayName"),
                    properties.getProperty("skill." + skillId + ".purpose", "Assigned governed skill").trim(),
                    properties.getProperty("skill." + skillId + ".whenToUse", "Use when the agent request matches this assigned skill.").trim(),
                    required(properties, "skill." + skillId + ".resource"),
                    required(properties, "skill." + skillId + ".checksum")))
        .toList();

    return new ReferenceAgentBehaviorSeedManifest(
        required(properties, "seedBundleId"),
        required(properties, "appVersion"),
        required(properties, "contentVersion"),
        required(properties, "agentDefinitionId"),
        required(properties, "agentDisplayName"),
        required(properties, "modelConfigRef"),
        required(properties, "authorityLevel"),
        required(properties, "promptDocumentId"),
        required(properties, "promptVersionId"),
        required(properties, "promptResource"),
        required(properties, "promptChecksum"),
        properties.getProperty("previousPromptChecksum", ""),
        required(properties, "skillManifestId"),
        required(properties, "skillManifestVersionId"),
        required(properties, "toolBoundaryId"),
        required(properties, "toolBoundaryVersionId"),
        skills);
  }

  private static String required(Properties properties, String key) {
    var value = properties.getProperty(key);
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Missing seed manifest property: " + key);
    }
    return value.trim();
  }
}
