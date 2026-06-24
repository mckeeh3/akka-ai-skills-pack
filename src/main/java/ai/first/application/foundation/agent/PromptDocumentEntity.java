package ai.first.application.foundation.agent;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.TypeName;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.GovernedArtifactLifecycleFact;
import ai.first.domain.foundation.agent.PromptDocument;
import ai.first.domain.foundation.agent.PromptVersion;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** First-class tenant-scoped governed prompt document lifecycle and active version carrier. */
@Component(id = "prompt-document")
public class PromptDocumentEntity extends EventSourcedEntity<PromptDocumentEntity.State, PromptDocumentEntity.Event> {
  public static String entityId(String tenantId, String promptDocumentId) {
    return tenantId + "__" + promptDocumentId;
  }

  @Override
  public State emptyState() {
    return State.empty();
  }

  public ReadOnlyEffect<Optional<PromptDocument>> detail(DocumentQuery query) {
    return effects().reply(currentState().documentForTenant(query.tenantId()));
  }

  public ReadOnlyEffect<Optional<PromptDocument>> activeRuntimeLookup(DocumentQuery query) {
    return effects().reply(currentState().documentForTenant(query.tenantId())
        .filter(document -> document.status() == AgentLifecycleStatus.ACTIVE));
  }

  public ReadOnlyEffect<Optional<PromptVersion>> version(VersionQuery query) {
    return effects().reply(currentState().versionForTenant(query.tenantId(), query.version()));
  }

  public ReadOnlyEffect<List<PromptVersion>> versions(DocumentQuery query) {
    return effects().reply(currentState().versionsForTenant(query.tenantId()));
  }

  public Effect<PromptDocument> saveCurrentVersion(SaveVersion command) {
    var validation = validateSaveVersion(command);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    var existing = currentState().document();
    var nextVersion = existing.activeVersion() + 1;
    var savedAt = timestamp(command.createdAt());
    var updated = new PromptDocument(
        existing.tenantId(),
        existing.promptDocumentId(),
        existing.agentDefinitionId(),
        existing.title(),
        existing.promptType(),
        existing.status(),
        nextVersion,
        command.contentBody(),
        checksum(command.contentBody()),
        command.changeSummary(),
        existing.seedProvenance(),
        existing.createdAt(),
        savedAt);
    return effects()
        .persist(new Event.PromptDocumentSaved(updated, toVersion(updated, command.actorAccountId(), command.editSessionTranscriptSummary()), lifecycleFact(updated, command.actorAccountId(), command.editSessionTranscriptSummary())))
        .thenReply(State::document);
  }

  public Effect<PromptDocument> restoreVersion(RestoreVersion command) {
    var validation = validateRestoreVersion(command);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    var existing = currentState().document();
    var restoredFrom = currentState().versions().get(command.version());
    var nextVersion = existing.activeVersion() + 1;
    var savedAt = timestamp(command.createdAt());
    var changeSummary = "Restored from version " + command.version();
    var updated = new PromptDocument(
        existing.tenantId(),
        existing.promptDocumentId(),
        existing.agentDefinitionId(),
        existing.title(),
        existing.promptType(),
        existing.status(),
        nextVersion,
        restoredFrom.contentBody(),
        checksum(restoredFrom.contentBody()),
        changeSummary,
        existing.seedProvenance(),
        existing.createdAt(),
        savedAt);
    return effects()
        .persist(new Event.PromptDocumentSaved(updated, toVersion(updated, command.actorAccountId(), changeSummary), lifecycleFact(updated, command.actorAccountId(), changeSummary)))
        .thenReply(State::document);
  }

  public ReadOnlyEffect<List<GovernedArtifactLifecycleFact>> history(DocumentQuery query) {
    return effects().reply(currentState().historyForTenant(query.tenantId()));
  }

  /** Compatibility write path for seed import and repository adapters. */
  public Effect<PromptDocument> save(PromptDocument document) {
    var validation = validate(document);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    if (document.equals(currentState().document())) {
      return effects().persist(new Event.LifecycleFactAppended(noOpFact(document))).thenReply(State::document);
    }
    return effects()
        .persist(new Event.PromptDocumentSaved(document, toVersion(document), lifecycleFact(document)))
        .thenReply(State::document);
  }

  @Override
  public State applyEvent(Event event) {
    return switch (event) {
      case Event.PromptDocumentSaved saved -> currentState().save(saved.document(), saved.activeVersion(), saved.lifecycleFact());
      case Event.LifecycleFactAppended appended -> currentState().appendHistory(appended.lifecycleFact());
    };
  }

  public Effect<List<GovernedArtifactLifecycleFact>> appendLifecycleFact(GovernedArtifactLifecycleFact fact) {
    var validation = validateLifecycleFact(fact);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    return effects().persist(new Event.LifecycleFactAppended(fact)).thenReply(State::history);
  }

  private Optional<String> validateLifecycleFact(GovernedArtifactLifecycleFact fact) {
    if (fact == null) return Optional.of("lifecycle-fact-required");
    if (blank(fact.tenantId())) return Optional.of("tenant-required");
    if (fact.artifactType() != GovernedArtifactLifecycleFact.ArtifactType.PROMPT_DOCUMENT) return Optional.of("artifact-type-mismatch");
    if (!currentState().documentForTenant(fact.tenantId()).map(document -> document.promptDocumentId().equals(fact.artifactId())).orElse(false)) {
      return Optional.of("artifact-not-found-for-tenant");
    }
    if (fact.transition() == null) return Optional.of("transition-required");
    if (fact.occurredAt() == null) return Optional.of("occurred-at-required");
    return Optional.empty();
  }

  private Optional<String> validateSaveVersion(SaveVersion command) {
    if (command == null) return Optional.of("save-version-command-required");
    if (blank(command.tenantId())) return Optional.of("tenant-required");
    if (blank(command.documentId())) return Optional.of("prompt-document-id-required");
    if (blank(command.contentBody())) return Optional.of("prompt-content-required");
    if (containsSecretLikeContent(command.contentBody())) return Optional.of("prompt-content-contains-secret-like-value");
    var existing = currentState().documentForTenant(command.tenantId())
        .filter(document -> document.promptDocumentId().equals(command.documentId()));
    if (existing.isEmpty()) return Optional.of("prompt-document-not-found");
    if (existing.get().activeVersion() != command.expectedCurrentVersion()) return Optional.of("stale-current-version");
    return Optional.empty();
  }

  private Optional<String> validateRestoreVersion(RestoreVersion command) {
    if (command == null) return Optional.of("restore-version-command-required");
    if (blank(command.tenantId())) return Optional.of("tenant-required");
    if (blank(command.documentId())) return Optional.of("prompt-document-id-required");
    var existing = currentState().documentForTenant(command.tenantId())
        .filter(document -> document.promptDocumentId().equals(command.documentId()));
    if (existing.isEmpty()) return Optional.of("prompt-document-not-found");
    if (command.version() < 1 || command.version() >= existing.get().activeVersion()) return Optional.of("historical-version-required");
    if (!currentState().versions().containsKey(command.version())) return Optional.of("prompt-version-not-found");
    return Optional.empty();
  }

  private Optional<String> validate(PromptDocument document) {
    if (document == null) return Optional.of("prompt-document-required");
    if (blank(document.tenantId())) return Optional.of("tenant-required");
    if (blank(document.promptDocumentId())) return Optional.of("prompt-document-id-required");
    if (blank(document.agentDefinitionId())) return Optional.of("agent-definition-id-required");
    if (document.status() == null) return Optional.of("lifecycle-status-required");
    if (document.activeVersion() < 1) return Optional.of("active-version-required");
    if (blank(document.contentBody())) return Optional.of("prompt-content-required");
    if (containsSecretLikeContent(document.contentBody())) return Optional.of("prompt-content-contains-secret-like-value");
    return Optional.empty();
  }

  private GovernedArtifactLifecycleFact lifecycleFact(PromptDocument document) {
    return lifecycleFact(document, lifecycleActor(document), lifecycleTranscript(document));
  }

  private GovernedArtifactLifecycleFact lifecycleFact(PromptDocument document, String actorAccountId, String reason) {
    var previous = currentState().document();
    return GovernedArtifactLifecycleFact.of(
        document.tenantId(),
        GovernedArtifactLifecycleFact.ArtifactType.PROMPT_DOCUMENT,
        document.promptDocumentId(),
        document.agentDefinitionId(),
        transition(previous == null ? null : previous.status(), document.status(), document.seedProvenance() != null),
        previous == null ? null : previous.status(),
        document.status(),
        previous == null ? 0 : previous.activeVersion(),
        document.activeVersion(),
        document.promptDocumentId() + ":v" + document.activeVersion(),
        document.contentChecksum(),
        actorAccountId,
        document.seedProvenance() == null ? null : document.seedProvenance().correlationId(),
        reason,
        false,
        document.updatedAt());
  }

  private GovernedArtifactLifecycleFact noOpFact(PromptDocument document) {
    return GovernedArtifactLifecycleFact.of(
        document.tenantId(),
        GovernedArtifactLifecycleFact.ArtifactType.PROMPT_DOCUMENT,
        document.promptDocumentId(),
        document.agentDefinitionId(),
        GovernedArtifactLifecycleFact.Transition.NO_OP,
        document.status(),
        document.status(),
        document.activeVersion(),
        document.activeVersion(),
        document.promptDocumentId() + ":v" + document.activeVersion(),
        document.contentChecksum(),
        "system",
        document.seedProvenance() == null ? null : document.seedProvenance().correlationId(),
        "duplicate-save-no-op",
        false,
        document.updatedAt());
  }

  private static GovernedArtifactLifecycleFact.Transition transition(AgentLifecycleStatus previous, AgentLifecycleStatus next, boolean seeded) {
    if (previous == null && seeded) return GovernedArtifactLifecycleFact.Transition.SEED_IMPORTED;
    if (next == AgentLifecycleStatus.ACTIVE) return GovernedArtifactLifecycleFact.Transition.ACTIVATED;
    if (previous == null || next == AgentLifecycleStatus.DRAFT) return GovernedArtifactLifecycleFact.Transition.DRAFTED;
    if (next == AgentLifecycleStatus.DISABLED) return GovernedArtifactLifecycleFact.Transition.DEPRECATED;
    if (next == AgentLifecycleStatus.ARCHIVED) return GovernedArtifactLifecycleFact.Transition.ARCHIVED;
    return GovernedArtifactLifecycleFact.Transition.REVIEWED;
  }

  private static PromptVersion toVersion(PromptDocument document) {
    return toVersion(document, lifecycleActor(document), lifecycleTranscript(document));
  }

  private static PromptVersion toVersion(PromptDocument document, String actorAccountId, String editSessionTranscriptSummary) {
    return new PromptVersion(
        document.tenantId(),
        document.promptDocumentId(),
        document.activeVersion(),
        document.agentDefinitionId(),
        document.title(),
        document.promptType(),
        document.status(),
        document.contentBody(),
        document.contentChecksum(),
        document.changeSummary(),
        document.seedProvenance(),
        document.updatedAt(),
        document.updatedAt(),
        document.status() == AgentLifecycleStatus.ACTIVE ? document.updatedAt() : null,
        actorAccountId,
        editSessionTranscriptSummary);
  }

  private static String lifecycleActor(PromptDocument document) {
    return document.seedProvenance() == null || blank(document.seedProvenance().importerActor())
        ? "system"
        : document.seedProvenance().importerActor();
  }

  private static String lifecycleTranscript(PromptDocument document) {
    return document.changeSummary();
  }

  private static Instant timestamp(Instant value) {
    return value == null ? Instant.EPOCH : value;
  }

  private static String checksum(String content) {
    try {
      return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(String.valueOf(content).getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }

  static boolean containsSecretLikeContent(String content) {
    return content != null && content.matches("(?is).*(api[_-]?key|secret|token|password)\\s*[:=]\\s*[^\\s]+.*");
  }

  public record State(PromptDocument document, Map<Integer, PromptVersion> versions, List<GovernedArtifactLifecycleFact> history) {
    static State empty() {
      return new State(null, Map.of(), List.of());
    }

    public State {
      versions = Map.copyOf(versions == null ? Map.of() : versions);
      history = List.copyOf(history == null ? List.of() : history);
    }

    State save(PromptDocument document, PromptVersion version, GovernedArtifactLifecycleFact lifecycleFact) {
      var updatedVersions = new java.util.LinkedHashMap<>(versions);
      updatedVersions.put(version.version(), version);
      var updatedHistory = new java.util.ArrayList<>(history);
      updatedHistory.add(lifecycleFact);
      return new State(document, updatedVersions, updatedHistory);
    }

    State appendHistory(GovernedArtifactLifecycleFact lifecycleFact) {
      var updatedHistory = new java.util.ArrayList<>(history);
      updatedHistory.add(lifecycleFact);
      return new State(document, versions, updatedHistory);
    }

    List<GovernedArtifactLifecycleFact> historyForTenant(String tenantId) {
      return history.stream().filter(fact -> fact.tenantId().equals(tenantId)).toList();
    }

    Optional<PromptDocument> documentForTenant(String tenantId) {
      return Optional.ofNullable(document).filter(candidate -> candidate.tenantId().equals(tenantId));
    }

    Optional<PromptVersion> versionForTenant(String tenantId, int version) {
      return Optional.ofNullable(versions.get(version)).filter(candidate -> candidate.tenantId().equals(tenantId));
    }

    List<PromptVersion> versionsForTenant(String tenantId) {
      return versions.values().stream()
          .filter(candidate -> candidate.tenantId().equals(tenantId))
          .sorted(java.util.Comparator.comparingInt(PromptVersion::version))
          .toList();
    }
  }

  public sealed interface Event {
    PromptDocument document();
    PromptVersion activeVersion();
    default GovernedArtifactLifecycleFact lifecycleFact() { return null; }

    @TypeName("prompt-document-saved")
    record PromptDocumentSaved(PromptDocument document, PromptVersion activeVersion, GovernedArtifactLifecycleFact lifecycleFact) implements Event {}
    @TypeName("prompt-document-lifecycle-fact-appended")
    record LifecycleFactAppended(GovernedArtifactLifecycleFact lifecycleFact) implements Event {
      @Override public PromptDocument document() { return null; }
      @Override public PromptVersion activeVersion() { return null; }
    }
  }

  public record DocumentQuery(String tenantId, String documentId) {}
  public record VersionQuery(String tenantId, String documentId, int version) {}
  public record SaveVersion(String tenantId, String documentId, int expectedCurrentVersion, String contentBody, String actorAccountId, String changeSummary, String editSessionTranscriptSummary, Instant createdAt) {}
  public record RestoreVersion(String tenantId, String documentId, int version, String actorAccountId, Instant createdAt) {}
}
