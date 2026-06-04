package ai.first.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.TypeName;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.GovernedArtifactLifecycleFact;
import ai.first.domain.foundation.agent.AgentSkillManifest;
import java.util.List;
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

  public ReadOnlyEffect<List<GovernedArtifactLifecycleFact>> history(ManifestQuery query) {
    return effects().reply(currentState().historyForTenant(query.tenantId()));
  }

  /** Compatibility write path for seed import and repository adapters. */
  public Effect<AgentSkillManifest> save(AgentSkillManifest manifest) {
    var validation = validate(manifest);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    if (manifest.equals(currentState().manifest())) {
      return effects().persist(new Event.LifecycleFactAppended(noOpFact(manifest))).thenReply(State::manifest);
    }
    return effects().persist(new Event.SkillManifestSaved(manifest, lifecycleFact(manifest))).thenReply(State::manifest);
  }

  @Override
  public State applyEvent(Event event) {
    return switch (event) {
      case Event.SkillManifestSaved saved -> currentState().save(saved.manifest(), saved.lifecycleFact());
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
    if (fact.artifactType() != GovernedArtifactLifecycleFact.ArtifactType.AGENT_SKILL_MANIFEST) return Optional.of("artifact-type-mismatch");
    if (!currentState().manifestForTenant(fact.tenantId()).map(manifest -> manifest.manifestId().equals(fact.artifactId())).orElse(false)) {
      return Optional.of("artifact-not-found-for-tenant");
    }
    if (fact.transition() == null) return Optional.of("transition-required");
    if (fact.occurredAt() == null) return Optional.of("occurred-at-required");
    return Optional.empty();
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

  private GovernedArtifactLifecycleFact lifecycleFact(AgentSkillManifest manifest) {
    var previous = currentState().manifest();
    return GovernedArtifactLifecycleFact.of(
        manifest.tenantId(),
        GovernedArtifactLifecycleFact.ArtifactType.AGENT_SKILL_MANIFEST,
        manifest.manifestId(),
        manifest.agentDefinitionId(),
        transition(previous == null ? null : previous.status(), manifest.status(), manifest.seedProvenance() != null),
        previous == null ? null : previous.status(),
        manifest.status(),
        previous == null ? 0 : previous.manifestVersion(),
        manifest.manifestVersion(),
        manifest.manifestId() + ":v" + manifest.manifestVersion(),
        manifest.compactManifestChecksum(),
        "system",
        manifest.seedProvenance() == null ? null : manifest.seedProvenance().correlationId(),
        previous == null ? "initial lifecycle import" : "lifecycle update",
        false,
        manifest.updatedAt());
  }

  private GovernedArtifactLifecycleFact noOpFact(AgentSkillManifest manifest) {
    return GovernedArtifactLifecycleFact.of(
        manifest.tenantId(),
        GovernedArtifactLifecycleFact.ArtifactType.AGENT_SKILL_MANIFEST,
        manifest.manifestId(),
        manifest.agentDefinitionId(),
        GovernedArtifactLifecycleFact.Transition.NO_OP,
        manifest.status(),
        manifest.status(),
        manifest.manifestVersion(),
        manifest.manifestVersion(),
        manifest.manifestId() + ":v" + manifest.manifestVersion(),
        manifest.compactManifestChecksum(),
        "system",
        manifest.seedProvenance() == null ? null : manifest.seedProvenance().correlationId(),
        "duplicate-save-no-op",
        false,
        manifest.updatedAt());
  }

  private static GovernedArtifactLifecycleFact.Transition transition(AgentLifecycleStatus previous, AgentLifecycleStatus next, boolean seeded) {
    if (previous == null && seeded) return GovernedArtifactLifecycleFact.Transition.SEED_IMPORTED;
    if (next == AgentLifecycleStatus.ACTIVE) return GovernedArtifactLifecycleFact.Transition.ACTIVATED;
    if (previous == null || next == AgentLifecycleStatus.DRAFT) return GovernedArtifactLifecycleFact.Transition.DRAFTED;
    if (next == AgentLifecycleStatus.DISABLED || next == AgentLifecycleStatus.DEPRECATED) return GovernedArtifactLifecycleFact.Transition.DEPRECATED;
    if (next == AgentLifecycleStatus.ARCHIVED) return GovernedArtifactLifecycleFact.Transition.ARCHIVED;
    if (next == AgentLifecycleStatus.APPROVED) return GovernedArtifactLifecycleFact.Transition.APPROVED;
    if (next == AgentLifecycleStatus.IN_REVIEW) return GovernedArtifactLifecycleFact.Transition.SUBMITTED;
    return GovernedArtifactLifecycleFact.Transition.REVIEWED;
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }

  public record State(AgentSkillManifest manifest, List<GovernedArtifactLifecycleFact> history) {
    static State empty() {
      return new State(null, List.of());
    }

    public State {
      history = List.copyOf(history == null ? List.of() : history);
    }

    State save(AgentSkillManifest manifest, GovernedArtifactLifecycleFact lifecycleFact) {
      var updatedHistory = new java.util.ArrayList<>(history);
      updatedHistory.add(lifecycleFact);
      return new State(manifest, updatedHistory);
    }

    State appendHistory(GovernedArtifactLifecycleFact lifecycleFact) {
      var updatedHistory = new java.util.ArrayList<>(history);
      updatedHistory.add(lifecycleFact);
      return new State(manifest, updatedHistory);
    }

    List<GovernedArtifactLifecycleFact> historyForTenant(String tenantId) {
      return history.stream().filter(fact -> fact.tenantId().equals(tenantId)).toList();
    }

    Optional<AgentSkillManifest> manifestForTenant(String tenantId) {
      return Optional.ofNullable(manifest).filter(candidate -> candidate.tenantId().equals(tenantId));
    }
  }

  public sealed interface Event {
    AgentSkillManifest manifest();
    default GovernedArtifactLifecycleFact lifecycleFact() { return null; }

    @TypeName("agent-skill-manifest-saved")
    record SkillManifestSaved(AgentSkillManifest manifest, GovernedArtifactLifecycleFact lifecycleFact) implements Event {}
    @TypeName("agent-skill-manifest-lifecycle-fact-appended")
    record LifecycleFactAppended(GovernedArtifactLifecycleFact lifecycleFact) implements Event {
      @Override public AgentSkillManifest manifest() { return null; }
    }
  }

  public record ManifestQuery(String tenantId, String manifestId) {}
}
