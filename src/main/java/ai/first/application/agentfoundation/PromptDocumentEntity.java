package ai.first.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.TypeName;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.GovernedArtifactLifecycleFact;
import ai.first.domain.foundation.agent.PromptDocument;
import ai.first.domain.foundation.agent.PromptVersion;
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
        "system",
        document.seedProvenance() == null ? null : document.seedProvenance().correlationId(),
        document.changeSummary(),
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
        document.status() == AgentLifecycleStatus.ACTIVE ? document.updatedAt() : null);
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
}
