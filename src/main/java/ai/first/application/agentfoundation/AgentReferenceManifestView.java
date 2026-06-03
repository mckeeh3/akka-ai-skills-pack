package ai.first.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import ai.first.domain.agentfoundation.AgentReferenceManifest;
import java.time.Instant;
import java.util.List;

/** Event-sourced reference-manifest projection for compact runtime lookup and Agent Admin inspection. */
@Component(id = "agent-reference-manifest-view")
public class AgentReferenceManifestView extends View {
  public record ReferenceManifestRow(
      String tenantId,
      String manifestId,
      String agentDefinitionId,
      String workstreamExpertBundleId,
      String lifecycleStatus,
      int manifestVersion,
      List<CompactReferenceEntry> compactReferenceEntries,
      int entryCount,
      String compactManifestChecksum,
      Instant createdAt,
      Instant updatedAt) {
    static ReferenceManifestRow from(AgentReferenceManifest manifest) {
      var compactEntries = manifest.entries().stream().map(CompactReferenceEntry::from).toList();
      return new ReferenceManifestRow(
          manifest.tenantId(),
          manifest.manifestId(),
          manifest.agentDefinitionId(),
          manifest.workstreamExpertBundleId(),
          manifest.status().name(),
          manifest.manifestVersion(),
          compactEntries,
          compactEntries.size(),
          manifest.compactManifestChecksum(),
          manifest.createdAt(),
          manifest.updatedAt());
    }
  }

  public record CompactReferenceEntry(
      String stableReferenceId,
      String referenceDocumentId,
      int pinnedVersion,
      String title,
      String summary,
      String whenToConsult,
      String allowedUse,
      String accessLevel) {
    static CompactReferenceEntry from(AgentReferenceManifest.Entry entry) {
      return new CompactReferenceEntry(
          entry.stableReferenceId(),
          entry.referenceDocumentId(),
          entry.pinnedVersion(),
          entry.title(),
          entry.summary(),
          entry.whenToConsult(),
          entry.allowedUse(),
          entry.accessLevel());
    }
  }

  public record ManifestDetailQuery(String tenantId, String manifestId) {}
  public record ActiveManifestQuery(String tenantId, String manifestId, String lifecycleStatus) {
    public static ActiveManifestQuery active(String tenantId, String manifestId) {
      return new ActiveManifestQuery(tenantId, manifestId, "ACTIVE");
    }
  }
  public record AgentManifestQuery(String tenantId, String agentDefinitionId) {}
  public record ManifestRows(List<ReferenceManifestRow> manifests) {}

  @Consume.FromEventSourcedEntity(AgentReferenceManifestEntity.class)
  public static class ReferenceManifestsUpdater extends TableUpdater<ReferenceManifestRow> {
    public Effect<ReferenceManifestRow> onEvent(AgentReferenceManifestEntity.Event event) {
      return effects().updateRow(ReferenceManifestRow.from(event.manifest()));
    }
  }

  @Query("""
      SELECT *
      FROM agent_reference_manifest_view
      WHERE tenantId = :tenantId AND manifestId = :manifestId
      """)
  public QueryEffect<ReferenceManifestRow> getDetail(ManifestDetailQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT *
      FROM agent_reference_manifest_view
      WHERE tenantId = :tenantId AND manifestId = :manifestId AND lifecycleStatus = :lifecycleStatus
      """)
  public QueryEffect<ReferenceManifestRow> activeCompactLookup(ActiveManifestQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT * AS manifests
      FROM agent_reference_manifest_view
      WHERE tenantId = :tenantId AND agentDefinitionId = :agentDefinitionId
      """)
  public QueryEffect<ManifestRows> byAgent(AgentManifestQuery query) {
    return queryResult();
  }
}
