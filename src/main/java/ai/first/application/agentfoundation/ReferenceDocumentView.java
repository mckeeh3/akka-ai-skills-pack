package ai.first.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import ai.first.domain.foundation.agent.ReferenceDocument;
import java.time.Instant;
import java.util.List;

/** Event-sourced reference document projection for catalog, active lookup, and evidence surfaces. */
@Component(id = "reference-document-view")
public class ReferenceDocumentView extends View {
  public record ReferenceDocumentRow(
      String tenantId,
      String referenceDocumentId,
      String stableReferenceId,
      String title,
      String summary,
      String whenToConsult,
      String referenceType,
      String accessLevel,
      List<String> tags,
      String lifecycleStatus,
      int activeVersion,
      String contentChecksum,
      Instant createdAt,
      Instant updatedAt) {
    static ReferenceDocumentRow from(ReferenceDocument document) {
      return new ReferenceDocumentRow(
          document.tenantId(),
          document.referenceDocumentId(),
          document.stableReferenceId(),
          document.title(),
          document.summary(),
          document.whenToConsult(),
          document.referenceType().name(),
          document.accessLevel(),
          document.tags(),
          document.status().name(),
          document.activeVersion(),
          document.contentChecksum(),
          document.createdAt(),
          document.updatedAt());
    }
  }

  public record DocumentDetailQuery(String tenantId, String referenceDocumentId) {}
  public record ActiveDocumentQuery(String tenantId, String referenceDocumentId, String lifecycleStatus) {
    public static ActiveDocumentQuery active(String tenantId, String referenceDocumentId) {
      return new ActiveDocumentQuery(tenantId, referenceDocumentId, "ACTIVE");
    }
  }
  public record CatalogQuery(String tenantId) {}
  public record StableReferenceQuery(String tenantId, String stableReferenceId) {}
  public record DocumentRows(List<ReferenceDocumentRow> documents) {}

  @Consume.FromEventSourcedEntity(ReferenceDocumentEntity.class)
  public static class ReferenceDocumentsUpdater extends TableUpdater<ReferenceDocumentRow> {
    public Effect<ReferenceDocumentRow> onEvent(ReferenceDocumentEntity.Event event) {
      return effects().updateRow(ReferenceDocumentRow.from(event.document()));
    }
  }

  @Query("""
      SELECT *
      FROM reference_document_view
      WHERE tenantId = :tenantId AND referenceDocumentId = :referenceDocumentId
      """)
  public QueryEffect<ReferenceDocumentRow> getDetail(DocumentDetailQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT *
      FROM reference_document_view
      WHERE tenantId = :tenantId AND referenceDocumentId = :referenceDocumentId AND lifecycleStatus = :lifecycleStatus
      """)
  public QueryEffect<ReferenceDocumentRow> activeRuntimeLookup(ActiveDocumentQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT * AS documents
      FROM reference_document_view
      WHERE tenantId = :tenantId
      """)
  public QueryEffect<DocumentRows> catalog(CatalogQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT * AS documents
      FROM reference_document_view
      WHERE tenantId = :tenantId AND stableReferenceId = :stableReferenceId
      """)
  public QueryEffect<DocumentRows> byStableReferenceId(StableReferenceQuery query) {
    return queryResult();
  }
}
