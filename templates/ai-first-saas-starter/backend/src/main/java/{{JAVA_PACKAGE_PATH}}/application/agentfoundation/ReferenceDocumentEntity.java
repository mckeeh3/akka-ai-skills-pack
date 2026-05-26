package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.TypeName;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ReferenceDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ReferenceVersion;
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

  /** Compatibility write path for seed import and repository adapters. */
  public Effect<ReferenceDocument> save(ReferenceDocument document) {
    var validation = validate(document);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    if (document.equals(currentState().document())) {
      return effects().reply(document);
    }
    return effects()
        .persist(new Event.ReferenceDocumentSaved(document, toVersion(document)))
        .thenReply(State::document);
  }

  @Override
  public State applyEvent(Event event) {
    return switch (event) {
      case Event.ReferenceDocumentSaved saved -> currentState().save(saved.document(), saved.activeVersion());
    };
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

  public record State(ReferenceDocument document, Map<Integer, ReferenceVersion> versions) {
    static State empty() {
      return new State(null, Map.of());
    }

    public State {
      versions = Map.copyOf(versions == null ? Map.of() : versions);
    }

    State save(ReferenceDocument document, ReferenceVersion version) {
      var updatedVersions = new java.util.LinkedHashMap<>(versions);
      updatedVersions.put(version.version(), version);
      return new State(document, updatedVersions);
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

    @TypeName("reference-document-saved")
    record ReferenceDocumentSaved(ReferenceDocument document, ReferenceVersion activeVersion) implements Event {}
  }

  public record DocumentQuery(String tenantId, String documentId) {}
  public record VersionQuery(String tenantId, String documentId, int version) {}
}
