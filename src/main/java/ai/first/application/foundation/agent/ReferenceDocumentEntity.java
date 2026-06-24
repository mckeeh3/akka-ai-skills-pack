package ai.first.application.foundation.agent;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.TypeName;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.GovernedArtifactLifecycleFact;
import ai.first.domain.foundation.agent.ReferenceDocument;
import ai.first.domain.foundation.agent.ReferenceVersion;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** First-class tenant-scoped governed reference document lifecycle and active version carrier. */
@Component(id = "reference-document")
public class ReferenceDocumentEntity extends EventSourcedEntity<ReferenceDocumentEntity.State, ReferenceDocumentEntity.Event> {
  public static String entityId(String tenantId, String referenceDocumentId) {
    return tenantId + "__" + referenceDocumentId;
  }

  @Override
  public State emptyState() {
    return State.empty();
  }

  public ReadOnlyEffect<Optional<ReferenceDocument>> detail(DocumentQuery query) {
    return effects().reply(currentState().documentForTenant(query.tenantId()));
  }

  public ReadOnlyEffect<Optional<ReferenceDocument>> activeRuntimeLookup(DocumentQuery query) {
    return effects().reply(currentState().documentForTenant(query.tenantId())
        .filter(document -> document.status() == AgentLifecycleStatus.ACTIVE));
  }

  public ReadOnlyEffect<Optional<ReferenceVersion>> version(VersionQuery query) {
    return effects().reply(currentState().versionForTenant(query.tenantId(), query.version()));
  }

  public ReadOnlyEffect<List<ReferenceVersion>> versions(DocumentQuery query) {
    return effects().reply(currentState().versionsForTenant(query.tenantId()));
  }

  public ReadOnlyEffect<List<GovernedArtifactLifecycleFact>> history(DocumentQuery query) {
    return effects().reply(currentState().historyForTenant(query.tenantId()));
  }

  /** Compatibility write path for seed import and repository adapters. */
  public Effect<ReferenceDocument> save(ReferenceDocument document) {
    var validation = validate(document);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    if (currentState().deleted()) {
      return effects().error("reference-document-deleted");
    }
    if (document.equals(currentState().document())) {
      return effects().persist(new Event.LifecycleFactAppended(noOpFact(document))).thenReply(State::document);
    }
    return effects()
        .persist(new Event.ReferenceDocumentSaved(document, toVersion(document), lifecycleFact(document)))
        .thenReply(State::document);
  }

  public Effect<ReferenceDocument> saveCurrentVersion(SaveVersion command) {
    var validation = validateSaveVersion(command);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    var existing = currentState().document();
    var nextVersion = existing.activeVersion() + 1;
    var savedAt = timestamp(command.createdAt());
    var updated = new ReferenceDocument(
        existing.tenantId(),
        existing.referenceDocumentId(),
        existing.stableReferenceId(),
        existing.title(),
        existing.summary(),
        existing.whenToConsult(),
        existing.referenceType(),
        existing.accessLevel(),
        existing.tags(),
        existing.status(),
        nextVersion,
        command.contentBody(),
        checksum(command.contentBody()),
        existing.seedProvenance(),
        existing.createdAt(),
        savedAt);
    return effects()
        .persist(new Event.ReferenceDocumentSaved(updated, toVersion(updated, command.actorAccountId(), command.editSessionTranscriptSummary()), lifecycleFact(updated, command.actorAccountId(), command.editSessionTranscriptSummary())))
        .thenReply(State::document);
  }

  public Effect<ReferenceDocument> restoreVersion(RestoreVersion command) {
    var validation = validateRestoreVersion(command);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    var existing = currentState().document();
    var restoredFrom = currentState().versions().get(command.version());
    var nextVersion = existing.activeVersion() + 1;
    var savedAt = timestamp(command.createdAt());
    var changeSummary = "Restored from version " + command.version();
    var updated = new ReferenceDocument(
        existing.tenantId(),
        existing.referenceDocumentId(),
        existing.stableReferenceId(),
        existing.title(),
        existing.summary(),
        existing.whenToConsult(),
        existing.referenceType(),
        existing.accessLevel(),
        existing.tags(),
        existing.status(),
        nextVersion,
        restoredFrom.contentBody(),
        checksum(restoredFrom.contentBody()),
        existing.seedProvenance(),
        existing.createdAt(),
        savedAt);
    return effects()
        .persist(new Event.ReferenceDocumentSaved(updated, toVersion(updated, command.actorAccountId(), changeSummary), lifecycleFact(updated, command.actorAccountId(), changeSummary)))
        .thenReply(State::document);
  }

  public Effect<akka.Done> delete(DeleteDocument command) {
    var validation = validateDelete(command);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    return effects().persist(new Event.ReferenceDocumentDeleted(null)).thenReply(state -> akka.Done.getInstance());
  }

  @Override
  public State applyEvent(Event event) {
    return switch (event) {
      case Event.ReferenceDocumentSaved saved -> currentState().save(saved.document(), saved.activeVersion(), saved.lifecycleFact());
      case Event.LifecycleFactAppended appended -> currentState().appendHistory(appended.lifecycleFact());
      case Event.ReferenceDocumentDeleted ignored -> currentState().delete();
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
    if (fact.artifactType() != GovernedArtifactLifecycleFact.ArtifactType.REFERENCE_DOCUMENT) return Optional.of("artifact-type-mismatch");
    if (!currentState().documentForTenant(fact.tenantId()).map(document -> document.referenceDocumentId().equals(fact.artifactId())).orElse(false)) {
      return Optional.of("artifact-not-found-for-tenant");
    }
    if (fact.transition() == null) return Optional.of("transition-required");
    if (fact.occurredAt() == null) return Optional.of("occurred-at-required");
    return Optional.empty();
  }

  private Optional<String> validateSaveVersion(SaveVersion command) {
    if (command == null) return Optional.of("save-version-command-required");
    if (currentState().deleted()) return Optional.of("reference-document-deleted");
    if (blank(command.tenantId())) return Optional.of("tenant-required");
    if (blank(command.documentId())) return Optional.of("reference-document-id-required");
    if (blank(command.contentBody())) return Optional.of("reference-content-required");
    if (PromptDocumentEntity.containsSecretLikeContent(command.contentBody())) return Optional.of("reference-content-contains-secret-like-value");
    var existing = currentState().documentForTenant(command.tenantId())
        .filter(document -> document.referenceDocumentId().equals(command.documentId()));
    if (existing.isEmpty()) return Optional.of("reference-document-not-found");
    if (existing.get().activeVersion() != command.expectedCurrentVersion()) return Optional.of("stale-current-version");
    return Optional.empty();
  }

  private Optional<String> validateRestoreVersion(RestoreVersion command) {
    if (command == null) return Optional.of("restore-version-command-required");
    if (currentState().deleted()) return Optional.of("reference-document-deleted");
    if (blank(command.tenantId())) return Optional.of("tenant-required");
    if (blank(command.documentId())) return Optional.of("reference-document-id-required");
    var existing = currentState().documentForTenant(command.tenantId())
        .filter(document -> document.referenceDocumentId().equals(command.documentId()));
    if (existing.isEmpty()) return Optional.of("reference-document-not-found");
    if (command.version() < 1 || command.version() >= existing.get().activeVersion()) return Optional.of("historical-version-required");
    if (!currentState().versions().containsKey(command.version())) return Optional.of("reference-version-not-found");
    return Optional.empty();
  }

  private Optional<String> validateDelete(DeleteDocument command) {
    if (command == null) return Optional.of("delete-document-command-required");
    if (currentState().deleted()) return Optional.of("reference-document-deleted");
    if (blank(command.tenantId())) return Optional.of("tenant-required");
    if (blank(command.documentId())) return Optional.of("reference-document-id-required");
    if (currentState().documentForTenant(command.tenantId()).filter(document -> document.referenceDocumentId().equals(command.documentId())).isEmpty()) {
      return Optional.of("reference-document-not-found");
    }
    return Optional.empty();
  }

  private Optional<String> validate(ReferenceDocument document) {
    if (document == null) return Optional.of("reference-document-required");
    if (blank(document.tenantId())) return Optional.of("tenant-required");
    if (blank(document.referenceDocumentId())) return Optional.of("reference-document-id-required");
    if (blank(document.stableReferenceId())) return Optional.of("stable-reference-id-required");
    if (document.status() == null) return Optional.of("lifecycle-status-required");
    if (document.referenceType() == null) return Optional.of("reference-type-required");
    if (document.activeVersion() < 1) return Optional.of("active-version-required");
    if (blank(document.contentBody())) return Optional.of("reference-content-required");
    if (PromptDocumentEntity.containsSecretLikeContent(document.contentBody())) return Optional.of("reference-content-contains-secret-like-value");
    return Optional.empty();
  }

  private GovernedArtifactLifecycleFact lifecycleFact(ReferenceDocument document) {
    return lifecycleFact(document, lifecycleActor(document), lifecycleTranscript(document));
  }

  private GovernedArtifactLifecycleFact lifecycleFact(ReferenceDocument document, String actorAccountId, String reason) {
    var previous = currentState().document();
    return GovernedArtifactLifecycleFact.of(
        document.tenantId(),
        GovernedArtifactLifecycleFact.ArtifactType.REFERENCE_DOCUMENT,
        document.referenceDocumentId(),
        null,
        transition(previous == null ? null : previous.status(), document.status(), document.seedProvenance() != null),
        previous == null ? null : previous.status(),
        document.status(),
        previous == null ? 0 : previous.activeVersion(),
        document.activeVersion(),
        document.referenceDocumentId() + ":v" + document.activeVersion(),
        document.contentChecksum(),
        actorAccountId,
        document.seedProvenance() == null ? null : document.seedProvenance().correlationId(),
        reason,
        false,
        document.updatedAt());
  }

  private GovernedArtifactLifecycleFact noOpFact(ReferenceDocument document) {
    return GovernedArtifactLifecycleFact.of(
        document.tenantId(),
        GovernedArtifactLifecycleFact.ArtifactType.REFERENCE_DOCUMENT,
        document.referenceDocumentId(),
        null,
        GovernedArtifactLifecycleFact.Transition.NO_OP,
        document.status(),
        document.status(),
        document.activeVersion(),
        document.activeVersion(),
        document.referenceDocumentId() + ":v" + document.activeVersion(),
        document.contentChecksum(),
        lifecycleActor(document),
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

  private static ReferenceVersion toVersion(ReferenceDocument document) {
    return toVersion(document, lifecycleActor(document), lifecycleTranscript(document));
  }

  private static ReferenceVersion toVersion(ReferenceDocument document, String actorAccountId, String editSessionTranscriptSummary) {
    return new ReferenceVersion(
        document.tenantId(),
        document.referenceDocumentId(),
        document.stableReferenceId(),
        document.activeVersion(),
        document.title(),
        document.summary(),
        document.whenToConsult(),
        document.referenceType(),
        document.accessLevel(),
        document.tags(),
        document.status(),
        document.contentBody(),
        document.contentChecksum(),
        document.seedProvenance(),
        document.updatedAt(),
        document.updatedAt(),
        document.status() == AgentLifecycleStatus.ACTIVE ? document.updatedAt() : null,
        actorAccountId,
        editSessionTranscriptSummary);
  }

  private static String lifecycleActor(ReferenceDocument document) {
    return document.seedProvenance() == null || blank(document.seedProvenance().importerActor())
        ? "system"
        : document.seedProvenance().importerActor();
  }

  private static String lifecycleTranscript(ReferenceDocument document) {
    return document.summary();
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

  public record State(ReferenceDocument document, Map<Integer, ReferenceVersion> versions, List<GovernedArtifactLifecycleFact> history, boolean deleted) {
    static State empty() {
      return new State(null, Map.of(), List.of(), false);
    }

    public State {
      versions = Map.copyOf(versions == null ? Map.of() : versions);
      history = List.copyOf(history == null ? List.of() : history);
    }

    State save(ReferenceDocument document, ReferenceVersion version, GovernedArtifactLifecycleFact lifecycleFact) {
      var updatedVersions = new java.util.LinkedHashMap<>(versions);
      updatedVersions.put(version.version(), version);
      var updatedHistory = new java.util.ArrayList<>(history);
      updatedHistory.add(lifecycleFact);
      return new State(document, updatedVersions, updatedHistory, false);
    }

    State appendHistory(GovernedArtifactLifecycleFact lifecycleFact) {
      var updatedHistory = new java.util.ArrayList<>(history);
      updatedHistory.add(lifecycleFact);
      return new State(document, versions, updatedHistory, deleted);
    }

    State delete() {
      return new State(null, Map.of(), List.of(), true);
    }

    List<GovernedArtifactLifecycleFact> historyForTenant(String tenantId) {
      return deleted ? List.of() : history.stream().filter(fact -> fact.tenantId().equals(tenantId)).toList();
    }

    Optional<ReferenceDocument> documentForTenant(String tenantId) {
      return deleted ? Optional.empty() : Optional.ofNullable(document).filter(candidate -> candidate.tenantId().equals(tenantId));
    }

    Optional<ReferenceVersion> versionForTenant(String tenantId, int version) {
      return deleted ? Optional.empty() : Optional.ofNullable(versions.get(version)).filter(candidate -> candidate.tenantId().equals(tenantId));
    }

    List<ReferenceVersion> versionsForTenant(String tenantId) {
      return deleted ? List.of() : versions.values().stream()
          .filter(candidate -> candidate.tenantId().equals(tenantId))
          .sorted(Comparator.comparingInt(ReferenceVersion::version))
          .toList();
    }
  }

  public sealed interface Event {
    ReferenceDocument document();
    ReferenceVersion activeVersion();
    default GovernedArtifactLifecycleFact lifecycleFact() { return null; }

    @TypeName("reference-document-saved")
    record ReferenceDocumentSaved(ReferenceDocument document, ReferenceVersion activeVersion, GovernedArtifactLifecycleFact lifecycleFact) implements Event {}
    @TypeName("reference-document-lifecycle-fact-appended")
    record LifecycleFactAppended(GovernedArtifactLifecycleFact lifecycleFact) implements Event {
      @Override public ReferenceDocument document() { return null; }
      @Override public ReferenceVersion activeVersion() { return null; }
    }
    @TypeName("reference-document-deleted")
    record ReferenceDocumentDeleted(ReferenceDocument document) implements Event {
      @Override public ReferenceVersion activeVersion() { return null; }
    }
  }

  public record DocumentQuery(String tenantId, String documentId) {}
  public record VersionQuery(String tenantId, String documentId, int version) {}
  public record SaveVersion(String tenantId, String documentId, int expectedCurrentVersion, String contentBody, String actorAccountId, String changeSummary, String editSessionTranscriptSummary, Instant createdAt) {}
  public record RestoreVersion(String tenantId, String documentId, int version, String actorAccountId, Instant createdAt) {}
  public record DeleteDocument(String tenantId, String documentId, String actorAccountId, Instant deletedAt) {}
}
