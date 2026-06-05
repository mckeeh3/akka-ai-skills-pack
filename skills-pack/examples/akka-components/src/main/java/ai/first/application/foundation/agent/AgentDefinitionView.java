package ai.first.application.foundation.agent;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import ai.first.domain.foundation.agent.AgentDefinition;
import java.time.Instant;
import java.util.List;

/**
 * Event-sourced AgentDefinition views for runtime lookup and Agent Admin catalog/detail surfaces.
 *
 * <p>Rows are tenant-scoped and intentionally contain lifecycle/placement metadata needed by the
 * runtime resolver, Agent Admin catalog filters, and workstream placement inspection. Protected
 * endpoints must still authorize the caller before invoking these scoped queries.
 */
@Component(id = "agent-definition-view")
public class AgentDefinitionView extends View {

  public record AgentDefinitionRow(
      String tenantId,
      String agentDefinitionId,
      String displayName,
      String description,
      String placement,
      boolean functionalAgent,
      String functionalAreaId,
      String authorityLevel,
      String lifecycleStatus,
      String promptDocumentId,
      int activePromptVersion,
      String skillManifestId,
      int activeSkillManifestVersion,
      String referenceManifestId,
      int activeReferenceManifestVersion,
      String toolBoundaryId,
      int activeToolBoundaryVersion,
      String modelConfigRefId,
      String modelPolicyRefId,
      String runtimeClassRef,
      List<String> traceRequirements,
      Instant createdAt,
      Instant updatedAt) {
    static AgentDefinitionRow from(AgentDefinition definition) {
      return new AgentDefinitionRow(
          definition.tenantId(),
          definition.agentDefinitionId(),
          definition.displayName(),
          definition.description(),
          definition.placement().name(),
          definition.placement() == AgentDefinition.Placement.FUNCTIONAL_CONTEXT_AREA,
          definition.functionalAreaId(),
          definition.authorityLevel().name(),
          definition.status().name(),
          definition.promptDocumentId(),
          definition.activePromptVersion(),
          definition.skillManifestId(),
          definition.activeSkillManifestVersion(),
          definition.referenceManifestId(),
          definition.activeReferenceManifestVersion(),
          definition.toolBoundaryId(),
          definition.activeToolBoundaryVersion(),
          definition.modelConfigRefId(),
          definition.modelPolicyRefId(),
          definition.runtimeClassRef(),
          definition.traceRequirements(),
          definition.createdAt(),
          definition.updatedAt());
    }
  }

  public record AgentDefinitionDetailQuery(String tenantId, String agentDefinitionId) {}

  public record AgentRuntimeLookupQuery(String tenantId, String agentDefinitionId, String lifecycleStatus) {
    public static AgentRuntimeLookupQuery active(String tenantId, String agentDefinitionId) {
      return new AgentRuntimeLookupQuery(tenantId, agentDefinitionId, "ACTIVE");
    }
  }

  public record AgentCatalogQuery(String tenantId) {}

  public record AgentLifecycleQuery(String tenantId, String lifecycleStatus) {}

  public record WorkstreamPlacementQuery(String tenantId, String functionalAreaId) {}

  public record AgentCatalogRows(List<AgentDefinitionRow> agents) {}

  @Consume.FromEventSourcedEntity(AgentDefinitionEntity.class)
  public static class AgentDefinitionsUpdater extends TableUpdater<AgentDefinitionRow> {
    public Effect<AgentDefinitionRow> onEvent(AgentDefinitionEntity.Event event) {
      return effects().updateRow(AgentDefinitionRow.from(event.definition()));
    }
  }

  @Query(
      """
      SELECT *
      FROM agent_definition_view
      WHERE tenantId = :tenantId AND agentDefinitionId = :agentDefinitionId
      """)
  public QueryEffect<AgentDefinitionRow> getDetail(AgentDefinitionDetailQuery query) {
    return queryResult();
  }

  @Query(
      """
      SELECT *
      FROM agent_definition_view
      WHERE tenantId = :tenantId AND agentDefinitionId = :agentDefinitionId AND lifecycleStatus = :lifecycleStatus
      """)
  public QueryEffect<AgentDefinitionRow> activeRuntimeLookup(AgentRuntimeLookupQuery query) {
    return queryResult();
  }

  @Query(
      """
      SELECT * AS agents
      FROM agent_definition_view
      WHERE tenantId = :tenantId
      """)
  public QueryEffect<AgentCatalogRows> agentCatalog(AgentCatalogQuery query) {
    return queryResult();
  }

  @Query(
      """
      SELECT * AS agents
      FROM agent_definition_view
      WHERE tenantId = :tenantId AND lifecycleStatus = :lifecycleStatus
      """)
  public QueryEffect<AgentCatalogRows> byLifecycle(AgentLifecycleQuery query) {
    return queryResult();
  }

  @Query(
      """
      SELECT * AS agents
      FROM agent_definition_view
      WHERE tenantId = :tenantId AND functionalAgent = true AND functionalAreaId = :functionalAreaId
      """)
  public QueryEffect<AgentCatalogRows> byWorkstreamPlacement(WorkstreamPlacementQuery query) {
    return queryResult();
  }
}
