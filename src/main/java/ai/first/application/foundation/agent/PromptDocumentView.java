package ai.first.application.foundation.agent;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import ai.first.domain.foundation.agent.PromptDocument;
import java.time.Instant;
import java.util.List;

/** Event-sourced prompt document projection for active runtime lookup, catalog, and version history. */
@Component(id = "prompt-document-view")
public class PromptDocumentView extends View {
  public record PromptDocumentRow(
      String tenantId,
      String promptDocumentId,
      String agentDefinitionId,
      String title,
      String promptType,
      String lifecycleStatus,
      int activeVersion,
      String contentChecksum,
      String changeSummary,
      Instant createdAt,
      Instant updatedAt) {
    static PromptDocumentRow from(PromptDocument document) {
      return new PromptDocumentRow(
          document.tenantId(),
          document.promptDocumentId(),
          document.agentDefinitionId(),
          document.title(),
          document.promptType(),
          document.status().name(),
          document.activeVersion(),
          document.contentChecksum(),
          document.changeSummary(),
          document.createdAt(),
          document.updatedAt());
    }
  }

  public record DocumentDetailQuery(String tenantId, String promptDocumentId) {}
  public record ActiveDocumentQuery(String tenantId, String promptDocumentId, String lifecycleStatus) {
    public static ActiveDocumentQuery active(String tenantId, String promptDocumentId) {
      return new ActiveDocumentQuery(tenantId, promptDocumentId, "ACTIVE");
    }
  }
  public record CatalogQuery(String tenantId) {}
  public record AgentUsageQuery(String tenantId, String agentDefinitionId) {}
  public record DocumentRows(List<PromptDocumentRow> documents) {}

  @Consume.FromEventSourcedEntity(PromptDocumentEntity.class)
  public static class PromptDocumentsUpdater extends TableUpdater<PromptDocumentRow> {
    public Effect<PromptDocumentRow> onEvent(PromptDocumentEntity.Event event) {
      return effects().updateRow(PromptDocumentRow.from(event.document()));
    }
  }

  @Query("""
      SELECT *
      FROM prompt_document_view
      WHERE tenantId = :tenantId AND promptDocumentId = :promptDocumentId
      """)
  public QueryEffect<PromptDocumentRow> getDetail(DocumentDetailQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT *
      FROM prompt_document_view
      WHERE tenantId = :tenantId AND promptDocumentId = :promptDocumentId AND lifecycleStatus = :lifecycleStatus
      """)
  public QueryEffect<PromptDocumentRow> activeRuntimeLookup(ActiveDocumentQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT * AS documents
      FROM prompt_document_view
      WHERE tenantId = :tenantId
      """)
  public QueryEffect<DocumentRows> catalog(CatalogQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT * AS documents
      FROM prompt_document_view
      WHERE tenantId = :tenantId AND agentDefinitionId = :agentDefinitionId
      """)
  public QueryEffect<DocumentRows> assignedAgentUsage(AgentUsageQuery query) {
    return queryResult();
  }
}
