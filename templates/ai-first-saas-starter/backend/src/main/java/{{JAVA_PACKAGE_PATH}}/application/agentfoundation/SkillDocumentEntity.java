package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.TypeName;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.SkillDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.SkillVersion;
import java.util.Map;
import java.util.Optional;

/** First-class tenant-scoped governed skill document lifecycle and active version carrier. */
@Component(id = "skill-document")
public class SkillDocumentEntity extends EventSourcedEntity<SkillDocumentEntity.State, SkillDocumentEntity.Event> {
  public static String entityId(String tenantId, String skillDocumentId) {
    return tenantId + "__" + skillDocumentId;
  }

  @Override
  public State emptyState() {
    return State.empty();
  }

  public ReadOnlyEffect<Optional<SkillDocument>> detail(DocumentQuery query) {
    return effects().reply(currentState().documentForTenant(query.tenantId()));
  }

  public ReadOnlyEffect<Optional<SkillDocument>> activeRuntimeLookup(DocumentQuery query) {
    return effects().reply(currentState().documentForTenant(query.tenantId())
        .filter(document -> document.status() == AgentLifecycleStatus.ACTIVE));
  }

  public ReadOnlyEffect<Optional<SkillVersion>> version(VersionQuery query) {
    return effects().reply(currentState().versionForTenant(query.tenantId(), query.version()));
  }

  /** Compatibility write path for seed import and repository adapters. */
  public Effect<SkillDocument> save(SkillDocument document) {
    var validation = validate(document);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    if (document.equals(currentState().document())) {
      return effects().reply(document);
    }
    return effects()
        .persist(new Event.SkillDocumentSaved(document, toVersion(document)))
        .thenReply(State::document);
  }

  @Override
  public State applyEvent(Event event) {
    return switch (event) {
      case Event.SkillDocumentSaved saved -> currentState().save(saved.document(), saved.activeVersion());
    };
  }

  private Optional<String> validate(SkillDocument document) {
    if (document == null) return Optional.of("skill-document-required");
    if (blank(document.tenantId())) return Optional.of("tenant-required");
    if (blank(document.skillDocumentId())) return Optional.of("skill-document-id-required");
    if (blank(document.stableSkillId())) return Optional.of("stable-skill-id-required");
    if (document.status() == null) return Optional.of("lifecycle-status-required");
    if (document.activeVersion() < 1) return Optional.of("active-version-required");
    if (blank(document.contentBody())) return Optional.of("skill-content-required");
    if (PromptDocumentEntity.containsSecretLikeContent(document.contentBody())) return Optional.of("skill-content-contains-secret-like-value");
    return Optional.empty();
  }

  private static SkillVersion toVersion(SkillDocument document) {
    return new SkillVersion(
        document.tenantId(),
        document.skillDocumentId(),
        document.stableSkillId(),
        document.activeVersion(),
        document.title(),
        document.purpose(),
        document.whenToUse(),
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

  public record State(SkillDocument document, Map<Integer, SkillVersion> versions) {
    static State empty() {
      return new State(null, Map.of());
    }

    public State {
      versions = Map.copyOf(versions == null ? Map.of() : versions);
    }

    State save(SkillDocument document, SkillVersion version) {
      var updatedVersions = new java.util.LinkedHashMap<>(versions);
      updatedVersions.put(version.version(), version);
      return new State(document, updatedVersions);
    }

    Optional<SkillDocument> documentForTenant(String tenantId) {
      return Optional.ofNullable(document).filter(candidate -> candidate.tenantId().equals(tenantId));
    }

    Optional<SkillVersion> versionForTenant(String tenantId, int version) {
      return Optional.ofNullable(versions.get(version)).filter(candidate -> candidate.tenantId().equals(tenantId));
    }
  }

  public sealed interface Event {
    SkillDocument document();
    SkillVersion activeVersion();

    @TypeName("skill-document-saved")
    record SkillDocumentSaved(SkillDocument document, SkillVersion activeVersion) implements Event {}
  }

  public record DocumentQuery(String tenantId, String documentId) {}
  public record VersionQuery(String tenantId, String documentId, int version) {}
}
