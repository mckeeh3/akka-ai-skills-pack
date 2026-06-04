package ai.first.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import ai.first.domain.foundation.agent.SkillDocument;
import java.time.Instant;
import java.util.List;

/** Event-sourced skill document projection for catalog, active lookup, and manifest assignment support. */
@Component(id = "skill-document-view")
public class SkillDocumentView extends View {
  public record SkillDocumentRow(
      String tenantId,
      String skillDocumentId,
      String stableSkillId,
      String title,
      String purpose,
      String whenToUse,
      List<String> tags,
      String lifecycleStatus,
      int activeVersion,
      String contentChecksum,
      Instant createdAt,
      Instant updatedAt) {
    static SkillDocumentRow from(SkillDocument document) {
      return new SkillDocumentRow(
          document.tenantId(),
          document.skillDocumentId(),
          document.stableSkillId(),
          document.title(),
          document.purpose(),
          document.whenToUse(),
          document.tags(),
          document.status().name(),
          document.activeVersion(),
          document.contentChecksum(),
          document.createdAt(),
          document.updatedAt());
    }
  }

  public record DocumentDetailQuery(String tenantId, String skillDocumentId) {}
  public record ActiveDocumentQuery(String tenantId, String skillDocumentId, String lifecycleStatus) {
    public static ActiveDocumentQuery active(String tenantId, String skillDocumentId) {
      return new ActiveDocumentQuery(tenantId, skillDocumentId, "ACTIVE");
    }
  }
  public record CatalogQuery(String tenantId) {}
  public record StableSkillQuery(String tenantId, String stableSkillId) {}
  public record DocumentRows(List<SkillDocumentRow> documents) {}

  @Consume.FromEventSourcedEntity(SkillDocumentEntity.class)
  public static class SkillDocumentsUpdater extends TableUpdater<SkillDocumentRow> {
    public Effect<SkillDocumentRow> onEvent(SkillDocumentEntity.Event event) {
      return effects().updateRow(SkillDocumentRow.from(event.document()));
    }
  }

  @Query("""
      SELECT *
      FROM skill_document_view
      WHERE tenantId = :tenantId AND skillDocumentId = :skillDocumentId
      """)
  public QueryEffect<SkillDocumentRow> getDetail(DocumentDetailQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT *
      FROM skill_document_view
      WHERE tenantId = :tenantId AND skillDocumentId = :skillDocumentId AND lifecycleStatus = :lifecycleStatus
      """)
  public QueryEffect<SkillDocumentRow> activeRuntimeLookup(ActiveDocumentQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT * AS documents
      FROM skill_document_view
      WHERE tenantId = :tenantId
      """)
  public QueryEffect<DocumentRows> catalog(CatalogQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT * AS documents
      FROM skill_document_view
      WHERE tenantId = :tenantId AND stableSkillId = :stableSkillId
      """)
  public QueryEffect<DocumentRows> byStableSkillId(StableSkillQuery query) {
    return queryResult();
  }
}
