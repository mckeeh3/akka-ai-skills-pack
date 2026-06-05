package com.example.application.agentfoundation;

import com.example.domain.agentfoundation.ReferenceAgentBehaviorSeedManifest;
import com.example.domain.agentfoundation.ReferenceAgentDefinition;
import com.example.domain.agentfoundation.ReferenceAgentSkillManifest;
import com.example.domain.agentfoundation.ReferencePromptDocument;
import com.example.domain.agentfoundation.ReferencePromptVersion;
import com.example.domain.agentfoundation.ReferenceSeededAgentBehaviorState;
import com.example.domain.agentfoundation.ReferenceSkillDocument;
import com.example.domain.agentfoundation.ReferenceSkillVersion;
import com.example.domain.agentfoundation.ReferenceToolPermissionBoundary;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reference-only loader for implementation-developed default agent behavior seed documents.
 *
 * <p>A production app would call equivalent logic from an install/tenant-bootstrap workflow or an
 * internal bootstrap action, then persist the resulting records through governed entities.
 */
public final class ReferenceAgentBehaviorSeedLoader {
  public static final String IMPORTER_ACTOR = "implementation-seed-importer";

  public ReferenceAgentBehaviorSeedManifest loadManifest(String classpathResource) {
    try (var inputStream = resourceStream(classpathResource)) {
      if (inputStream == null) {
        throw new IllegalArgumentException("Missing seed manifest resource: " + classpathResource);
      }
      return ReferenceAgentBehaviorSeedManifest.fromProperties(inputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public SeedImportResult importSeed(
      String tenantId,
      ReferenceAgentBehaviorSeedManifest manifest,
      ReferenceSeededAgentBehaviorState existing,
      String correlationId) {
    validateNonBlank(tenantId, "tenantId");
    validateNonBlank(correlationId, "correlationId");
    var promptContent = loadAndVerify(manifest.promptResource(), manifest.promptChecksum());
    var skillContents = new HashMap<String, String>();
    for (var skill : manifest.skills()) {
      skillContents.put(skill.skillDocumentId(), loadAndVerify(skill.resource(), skill.checksum()));
    }

    var agents = new HashMap<>(existing.agentDefinitions());
    var promptDocuments = new HashMap<>(existing.promptDocuments());
    var promptVersions = new HashMap<>(existing.promptVersions());
    var skillDocuments = new HashMap<>(existing.skillDocuments());
    var skillVersions = new HashMap<>(existing.skillVersions());
    var manifests = new HashMap<>(existing.skillManifests());
    var toolBoundaries = new HashMap<>(existing.toolBoundaries());
    var provenance = new HashMap<>(existing.provenance());
    var events = new java.util.ArrayList<String>();
    var proposals = new java.util.ArrayList<String>();

    var existingPromptDocument = promptDocuments.get(manifest.promptDocumentId());
    if (existingPromptDocument == null) {
      promptDocuments.put(
          manifest.promptDocumentId(),
          new ReferencePromptDocument(
              tenantId,
              manifest.promptDocumentId(),
              manifest.agentDisplayName() + " Prompt",
              manifest.promptVersionId(),
              true));
      promptVersions.put(
          manifest.promptVersionId(),
          new ReferencePromptVersion(
              tenantId,
              manifest.promptDocumentId(),
              manifest.promptVersionId(),
              ReferencePromptVersion.VersionStatus.ACTIVE,
              promptContent,
              manifest.promptChecksum()));
      provenance.put(
          "prompt:" + manifest.promptVersionId(),
          provenance("prompt", manifest.promptDocumentId(), manifest.promptVersionId(), manifest, manifest.promptChecksum(), correlationId));
      events.add("prompt.created:" + manifest.promptDocumentId());
    } else {
      var activePrompt = promptVersions.get(existingPromptDocument.activePromptVersionId());
      if (activePrompt != null && activePrompt.checksum().equals(manifest.promptChecksum())) {
        events.add("prompt.skipped-unchanged:" + manifest.promptDocumentId());
      } else if (!manifest.previousPromptChecksum().isBlank()
          && activePrompt != null
          && activePrompt.checksum().equals(manifest.previousPromptChecksum())) {
        promptDocuments.put(
            manifest.promptDocumentId(),
            new ReferencePromptDocument(
                tenantId,
                manifest.promptDocumentId(),
                existingPromptDocument.displayName(),
                manifest.promptVersionId(),
                true));
        promptVersions.put(
            manifest.promptVersionId(),
            new ReferencePromptVersion(
                tenantId,
                manifest.promptDocumentId(),
                manifest.promptVersionId(),
                ReferencePromptVersion.VersionStatus.ACTIVE,
                promptContent,
                manifest.promptChecksum()));
        provenance.put(
            "prompt:" + manifest.promptVersionId(),
            provenance("prompt", manifest.promptDocumentId(), manifest.promptVersionId(), manifest, manifest.promptChecksum(), correlationId));
        events.add("prompt.upgraded:" + manifest.promptDocumentId());
      } else {
        proposals.add("prompt.proposed-diff:" + manifest.promptDocumentId());
        events.add("prompt.customization-preserved:" + manifest.promptDocumentId());
      }
    }

    for (var skill : manifest.skills()) {
      if (!skillDocuments.containsKey(skill.skillDocumentId())) {
        skillDocuments.put(
            skill.skillDocumentId(),
            new ReferenceSkillDocument(
                tenantId, skill.skillDocumentId(), skill.displayName(), skill.skillVersionId(), true));
        skillVersions.put(
            skill.skillVersionId(),
            new ReferenceSkillVersion(
                tenantId,
                skill.skillDocumentId(),
                skill.skillVersionId(),
                ReferenceSkillVersion.VersionStatus.ACTIVE,
                skillContents.get(skill.skillDocumentId()),
                skill.checksum()));
        provenance.put(
            "skill:" + skill.skillVersionId(),
            provenance("skill", skill.skillDocumentId(), skill.skillVersionId(), manifest, skill.checksum(), correlationId));
        events.add("skill.created:" + skill.skillDocumentId());
      } else {
        events.add("skill.skipped-existing:" + skill.skillDocumentId());
      }
    }

    var skillRefs = new HashMap<String, String>();
    var skillEntries = new HashMap<String, ReferenceAgentSkillManifest.SkillEntry>();
    for (var skill : manifest.skills()) {
      skillRefs.put(skill.skillDocumentId(), skill.skillVersionId());
      skillEntries.put(
          skill.skillDocumentId(),
          new ReferenceAgentSkillManifest.SkillEntry(
              skill.skillDocumentId(), skill.displayName(), skill.purpose(), skill.whenToUse()));
    }
    if (!manifests.containsKey(manifest.skillManifestId())) {
      manifests.put(
          manifest.skillManifestId(),
          new ReferenceAgentSkillManifest(
              tenantId,
              manifest.skillManifestId(),
              manifest.skillManifestVersionId(),
              manifest.agentDefinitionId(),
              skillRefs.keySet(),
              skillRefs,
              skillEntries,
              true));
      provenance.put(
          "manifest:" + manifest.skillManifestVersionId(),
          provenance("manifest", manifest.skillManifestId(), manifest.skillManifestVersionId(), manifest, manifest.contentVersion(), correlationId));
      events.add("manifest.created:" + manifest.skillManifestId());
    } else {
      events.add("manifest.skipped-existing:" + manifest.skillManifestId());
    }

    if (!toolBoundaries.containsKey(manifest.toolBoundaryId())) {
      toolBoundaries.put(
          manifest.toolBoundaryId(),
          new ReferenceToolPermissionBoundary(
              tenantId,
              manifest.toolBoundaryId(),
              manifest.toolBoundaryVersionId(),
              manifest.agentDefinitionId(),
              Set.of(ReferenceSkillReadAuthorizer.READ_SKILL_TOOL_ID),
              Set.of("runtime", "test", "replay"),
              true));
      provenance.put(
          "toolBoundary:" + manifest.toolBoundaryVersionId(),
          provenance("toolBoundary", manifest.toolBoundaryId(), manifest.toolBoundaryVersionId(), manifest, manifest.contentVersion(), correlationId));
      events.add("tool-boundary.created:" + manifest.toolBoundaryId());
    } else {
      events.add("tool-boundary.skipped-existing:" + manifest.toolBoundaryId());
    }

    var activePromptVersionId = promptDocuments.get(manifest.promptDocumentId()).activePromptVersionId();
    if (!agents.containsKey(manifest.agentDefinitionId())) {
      agents.put(
          manifest.agentDefinitionId(),
          new ReferenceAgentDefinition(
              tenantId,
              manifest.agentDefinitionId(),
              manifest.agentDisplayName(),
              ReferenceAgentDefinition.LifecycleStatus.ACTIVE,
              manifest.promptDocumentId(),
              activePromptVersionId,
              manifest.skillManifestId(),
              manifest.toolBoundaryId(),
              manifest.modelConfigRef(),
              manifest.authorityLevel()));
      events.add("agent.created:" + manifest.agentDefinitionId());
    } else {
      var existingAgent = agents.get(manifest.agentDefinitionId());
      if (!existingAgent.activePromptVersionId().equals(activePromptVersionId)) {
        agents.put(
            manifest.agentDefinitionId(),
            new ReferenceAgentDefinition(
                existingAgent.tenantId(),
                existingAgent.agentDefinitionId(),
                existingAgent.displayName(),
                existingAgent.lifecycleStatus(),
                existingAgent.promptDocumentId(),
                activePromptVersionId,
                existingAgent.skillManifestId(),
                existingAgent.toolBoundaryId(),
                existingAgent.modelConfigRef(),
                existingAgent.authorityLevel()));
        events.add("agent.updated-prompt-reference:" + manifest.agentDefinitionId());
      } else {
        events.add("agent.skipped-existing:" + manifest.agentDefinitionId());
      }
    }

    return new SeedImportResult(
        new ReferenceSeededAgentBehaviorState(
            agents, promptDocuments, promptVersions, skillDocuments, skillVersions, manifests, toolBoundaries, provenance),
        List.copyOf(events),
        List.copyOf(proposals),
        correlationId);
  }

  private String loadAndVerify(String resource, String expectedChecksum) {
    var content = resourceText(resource);
    if (content.contains("BEGIN PRIVATE KEY") || content.contains("api_key=")) {
      throw new IllegalArgumentException("Seed resource contains secret-like content: " + resource);
    }
    var actualChecksum = sha256(content);
    if (!actualChecksum.equals(expectedChecksum)) {
      throw new IllegalArgumentException("Seed checksum mismatch for " + resource);
    }
    return content;
  }

  private String resourceText(String classpathResource) {
    try (var inputStream = resourceStream(classpathResource)) {
      if (inputStream == null) {
        throw new IllegalArgumentException("Missing seed resource: " + classpathResource);
      }
      return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private java.io.InputStream resourceStream(String classpathResource) {
    return Thread.currentThread().getContextClassLoader().getResourceAsStream(classpathResource);
  }

  private static ReferenceSeededAgentBehaviorState.SeedProvenance provenance(
      String artifactType,
      String artifactId,
      String versionId,
      ReferenceAgentBehaviorSeedManifest manifest,
      String checksum,
      String correlationId) {
    return new ReferenceSeededAgentBehaviorState.SeedProvenance(
        artifactType,
        artifactId,
        versionId,
        manifest.seedBundleId(),
        manifest.contentVersion(),
        checksum,
        IMPORTER_ACTOR,
        correlationId);
  }

  private static void validateNonBlank(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Missing " + field);
    }
  }

  public static String sha256(String value) {
    try {
      var digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(digest);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public record SeedImportResult(
      ReferenceSeededAgentBehaviorState state,
      List<String> events,
      List<String> proposedDiffs,
      String correlationId) {}
}
