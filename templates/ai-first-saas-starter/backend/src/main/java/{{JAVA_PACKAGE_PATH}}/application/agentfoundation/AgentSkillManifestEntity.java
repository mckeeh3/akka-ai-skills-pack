package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.TypeName;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentSkillManifest;
import java.util.Optional;

/** First-class tenant-scoped Event Sourced Entity for per-agent skill manifest lifecycle/current state. */
@Component(id = "agent-skill-manifest")
public class AgentSkillManifestEntity extends EventSourcedEntity<AgentSkillManifestEntity.State, AgentSkillManifestEntity.Event> {
  public static String entityId(String tenantId, String manifestId) {
    return tenantId + "__" + manifestId;
  }

  @Override
  public State emptyState() {
    return State.empty();
  }

  public ReadOnlyEffect<Optional<AgentSkillManifest>> detail(ManifestQuery query) {
    return effects().reply(currentState().manifestForTenant(query.tenantId()));
  }

  public ReadOnlyEffect<Optional<AgentSkillManifest>> activeRuntimeLookup(ManifestQuery query) {
    return effects().reply(currentState().manifestForTenant(query.tenantId())
        .filter(manifest -> manifest.status() == AgentLifecycleStatus.ACTIVE));
  }

  /** Compatibility write path for seed import and repository adapters. */
  public Effect<AgentSkillManifest> save(AgentSkillManifest manifest) {
    var validation = validate(manifest);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    if (manifest.equals(currentState().manifest())) {
      return effects().reply(manifest);
    }
    return effects().persist(new Event.SkillManifestSaved(manifest)).thenReply(State::manifest);
  }

  @Override
  public State applyEvent(Event event) {
    return switch (event) {
      case Event.SkillManifestSaved saved -> new State(saved.manifest());
    };
  }

  private Optional<String> validate(AgentSkillManifest manifest) {
    if (manifest == null) return Optional.of("skill-manifest-required");
    if (blank(manifest.tenantId())) return Optional.of("tenant-required");
    if (blank(manifest.manifestId())) return Optional.of("skill-manifest-id-required");
    if (blank(manifest.agentDefinitionId())) return Optional.of("agent-definition-id-required");
    if (manifest.status() == null) return Optional.of("lifecycle-status-required");
    if (manifest.manifestVersion() < 1) return Optional.of("manifest-version-required");
    for (var entry : manifest.entries()) {
      if (blank(entry.stableSkillId())) return Optional.of("stable-skill-id-required");
      if (blank(entry.skillDocumentId())) return Optional.of("skill-document-id-required");
      if (entry.pinnedVersion() < 1) return Optional.of("skill-pinned-version-required");
    }
    return Optional.empty();
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }

  public record State(AgentSkillManifest manifest) {
    static State empty() {
      return new State(null);
    }

    Optional<AgentSkillManifest> manifestForTenant(String tenantId) {
      return Optional.ofNullable(manifest).filter(candidate -> candidate.tenantId().equals(tenantId));
    }
  }

  public sealed interface Event {
    AgentSkillManifest manifest();

    @TypeName("agent-skill-manifest-saved")
    record SkillManifestSaved(AgentSkillManifest manifest) implements Event {}
  }

  public record ManifestQuery(String tenantId, String manifestId) {}
}
