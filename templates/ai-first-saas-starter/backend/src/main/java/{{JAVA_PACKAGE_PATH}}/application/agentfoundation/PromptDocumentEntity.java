package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.TypeName;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptVersion;
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

  /** Compatibility write path for seed import and repository adapters. */
  public Effect<PromptDocument> save(PromptDocument document) {
    var validation = validate(document);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    if (document.equals(currentState().document())) {
      return effects().reply(document);
    }
    return effects()
        .persist(new Event.PromptDocumentSaved(document, toVersion(document)))
        .thenReply(State::document);
  }

  @Override
  public State applyEvent(Event event) {
    return switch (event) {
      case Event.PromptDocumentSaved saved -> currentState().save(saved.document(), saved.activeVersion());
    };
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

  public record State(PromptDocument document, Map<Integer, PromptVersion> versions) {
    static State empty() {
      return new State(null, Map.of());
    }

    public State {
      versions = Map.copyOf(versions == null ? Map.of() : versions);
    }

    State save(PromptDocument document, PromptVersion version) {
      var updatedVersions = new java.util.LinkedHashMap<>(versions);
      updatedVersions.put(version.version(), version);
      return new State(document, updatedVersions);
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

    @TypeName("prompt-document-saved")
    record PromptDocumentSaved(PromptDocument document, PromptVersion activeVersion) implements Event {}
  }

  public record DocumentQuery(String tenantId, String documentId) {}
  public record VersionQuery(String tenantId, String documentId, int version) {}
}
