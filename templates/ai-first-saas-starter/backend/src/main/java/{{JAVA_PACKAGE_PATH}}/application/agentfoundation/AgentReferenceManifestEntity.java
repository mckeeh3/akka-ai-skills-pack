package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.TypeName;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentReferenceManifest;
import java.util.Optional;

/** First-class tenant-scoped Event Sourced Entity for per-agent reference manifest lifecycle/current state. */
@Component(id = "agent-reference-manifest")
public class AgentReferenceManifestEntity extends EventSourcedEntity<AgentReferenceManifestEntity.State, AgentReferenceManifestEntity.Event> {
  public static String entityId(String tenantId, String manifestId) {
    return tenantId + "__" + manifestId;
  }

  @Override
  public State emptyState() {
    return State.empty();
  }

  public ReadOnlyEffect<Optional<AgentReferenceManifest>> detail(ManifestQuery query) {
    return effects().reply(currentState().manifestForTenant(query.tenantId()));
  }

  public ReadOnlyEffect<Optional<AgentReferenceManifest>> activeRuntimeLookup(ManifestQuery query) {
    return effects().reply(currentState().manifestForTenant(query.tenantId())
        .filter(manifest -> manifest.status() == AgentLifecycleStatus.ACTIVE));
  }

  /** Compatibility write path for seed import and repository adapters. */
  public Effect<AgentReferenceManifest> save(AgentReferenceManifest manifest) {
    var validation = validate(manifest);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    if (manifest.equals(currentState().manifest())) {
      return effects().reply(manifest);
    }
    return effects().persist(new Event.ReferenceManifestSaved(manifest)).thenReply(State::manifest);
  }

  @Override
  public State applyEvent(Event event) {
    return switch (event) {
      case Event.ReferenceManifestSaved saved -> new State(saved.manifest());
    };
  }

  private Optional<String> validate(AgentReferenceManifest manifest) {
    if (manifest == null) return Optional.of("reference-manifest-required");
    if (blank(manifest.tenantId())) return Optional.of("tenant-required");
    if (blank(manifest.manifestId())) return Optional.of("reference-manifest-id-required");
    if (blank(manifest.agentDefinitionId())) return Optional.of("agent-definition-id-required");
    if (manifest.status() == null) return Optional.of("lifecycle-status-required");
    if (manifest.manifestVersion() < 1) return Optional.of("manifest-version-required");
    for (var entry : manifest.entries()) {
      if (blank(entry.stableReferenceId())) return Optional.of("stable-reference-id-required");
      if (blank(entry.referenceDocumentId())) return Optional.of("reference-document-id-required");
      if (entry.pinnedVersion() < 1) return Optional.of("reference-pinned-version-required");
      if (blank(entry.allowedUse())) return Optional.of("reference-allowed-use-required");
    }
    return Optional.empty();
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }

  public record State(AgentReferenceManifest manifest) {
    static State empty() {
      return new State(null);
    }

    Optional<AgentReferenceManifest> manifestForTenant(String tenantId) {
      return Optional.ofNullable(manifest).filter(candidate -> candidate.tenantId().equals(tenantId));
    }
  }

  public sealed interface Event {
    AgentReferenceManifest manifest();

    @TypeName("agent-reference-manifest-saved")
    record ReferenceManifestSaved(AgentReferenceManifest manifest) implements Event {}
  }

  public record ManifestQuery(String tenantId, String manifestId) {}
}
