package ai.first.application.foundation.agent;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.TypeName;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.GovernedArtifactLifecycleFact;
import ai.first.domain.foundation.agent.ReferenceDocument;
import ai.first.domain.foundation.agent.ReferenceVersion;
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

  public ReadOnlyEffect<List<GovernedArtifactLifecycleFact>> history(DocumentQuery query) {
    return effects().reply(currentState().historyForTenant(query.tenantId()));
  }

  /** Compatibility write path for seed import and repository adapters. */
  public Effect<ReferenceDocument> save(ReferenceDocument document) {
    var validation = validate(document);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    if (document.equals(currentState().document())) {
      return effects().persist(new Event.LifecycleFactAppended(noOpFact(document))).thenReply(State::document);
    }
    return effects()
        .persist(new Event.ReferenceDocumentSaved(document, toVersion(document), lifecycleFact(document)))
        .thenReply(State::document);
  }

  @Override
  public State applyEvent(Event event) {
    return switch (event) {
      case Event.ReferenceDocumentSaved saved -> currentState().save(saved.document(), saved.activeVersion(), saved.lifecycleFact());
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
    if (fact.artifactType() != GovernedArtifactLifecycleFact.ArtifactType.REFERENCE_DOCUMENT) return Optional.of("artifact-type-mismatch");
    if (!currentState().documentForTenant(fact.tenantId()).map(document -> document.referenceDocumentId().equals(fact.artifactId())).orElse(false)) {
      return Optional.of("artifact-not-found-for-tenant");
    }
    if (fact.transition() == null) return Optional.of("transition-required");
    if (fact.occurredAt() == null) return Optional.of("occurred-at-required");
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
        "system",
        document.seedProvenance() == null ? null : document.seedProvenance().correlationId(),
        document.summary(),
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

  private static ReferenceVersion toVersion(ReferenceDocument document) {
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
        document.status() == AgentLifecycleStatus.ACTIVE ? document.updatedAt() : null);
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }

  public record State(ReferenceDocument document, Map<Integer, ReferenceVersion> versions, List<GovernedArtifactLifecycleFact> history) {
    static State empty() {
      return new State(null, Map.of(), List.of());
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

    Optional<ReferenceDocument> documentForTenant(String tenantId) {
      return Optional.ofNullable(document).filter(candidate -> candidate.tenantId().equals(tenantId));
    }

    Optional<ReferenceVersion> versionForTenant(String tenantId, int version) {
      return Optional.ofNullable(versions.get(version)).filter(candidate -> candidate.tenantId().equals(tenantId));
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
  }

  public record DocumentQuery(String tenantId, String documentId) {}
  public record VersionQuery(String tenantId, String documentId, int version) {}
}
