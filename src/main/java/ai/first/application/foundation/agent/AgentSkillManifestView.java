package ai.first.application.foundation.agent;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import ai.first.domain.foundation.agent.AgentSkillManifest;
import java.time.Instant;
import java.util.List;

/** Event-sourced skill-manifest projection for compact runtime lookup and Agent Admin inspection. */
@Component(id = "agent-skill-manifest-view")
public class AgentSkillManifestView extends View {
  public record SkillManifestRow(
      String tenantId,
      String manifestId,
      String agentDefinitionId,
      String lifecycleStatus,
      int manifestVersion,
      List<CompactSkillEntry> compactSkillEntries,
      int entryCount,
      String compactManifestChecksum,
      Instant createdAt,
      Instant updatedAt) {
    static SkillManifestRow from(AgentSkillManifest manifest) {
      var compactEntries = manifest.entries().stream().map(CompactSkillEntry::from).toList();
      return new SkillManifestRow(
          manifest.tenantId(),
          manifest.manifestId(),
          manifest.agentDefinitionId(),
          manifest.status().name(),
          manifest.manifestVersion(),
          compactEntries,
          compactEntries.size(),
          manifest.compactManifestChecksum(),
          manifest.createdAt(),
          manifest.updatedAt());
    }
  }

  public record CompactSkillEntry(
      String stableSkillId,
      String skillDocumentId,
      int pinnedVersion,
      String title,
      String purpose,
      String whenToUse) {
    static CompactSkillEntry from(AgentSkillManifest.Entry entry) {
      return new CompactSkillEntry(
          entry.stableSkillId(),
          entry.skillDocumentId(),
          entry.pinnedVersion(),
          entry.title(),
          entry.purpose(),
          entry.whenToUse());
    }
  }

  public record ManifestDetailQuery(String tenantId, String manifestId) {}
  public record ActiveManifestQuery(String tenantId, String manifestId, String lifecycleStatus) {
    public static ActiveManifestQuery active(String tenantId, String manifestId) {
      return new ActiveManifestQuery(tenantId, manifestId, "ACTIVE");
    }
  }
  public record AgentManifestQuery(String tenantId, String agentDefinitionId) {}
  public record ManifestRows(List<SkillManifestRow> manifests) {}

  @Consume.FromEventSourcedEntity(AgentSkillManifestEntity.class)
  public static class SkillManifestsUpdater extends TableUpdater<SkillManifestRow> {
    public Effect<SkillManifestRow> onEvent(AgentSkillManifestEntity.Event event) {
      return effects().updateRow(SkillManifestRow.from(event.manifest()));
    }
  }

  @Query("""
      SELECT *
      FROM agent_skill_manifest_view
      WHERE tenantId = :tenantId AND manifestId = :manifestId
      """)
  public QueryEffect<SkillManifestRow> getDetail(ManifestDetailQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT *
      FROM agent_skill_manifest_view
      WHERE tenantId = :tenantId AND manifestId = :manifestId AND lifecycleStatus = :lifecycleStatus
      """)
  public QueryEffect<SkillManifestRow> activeCompactLookup(ActiveManifestQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT * AS manifests
      FROM agent_skill_manifest_view
      WHERE tenantId = :tenantId AND agentDefinitionId = :agentDefinitionId
      """)
  public QueryEffect<ManifestRows> byAgent(AgentManifestQuery query) {
    return queryResult();
  }
}
