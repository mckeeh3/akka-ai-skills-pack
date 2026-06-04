package ai.first.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

/** Event-sourced ToolPermissionBoundary projection for boundary detail, grant search, and Agent Admin inspection. */
@Component(id = "tool-boundary-grant-view")
public class ToolBoundaryGrantView extends View {
  public record ToolBoundaryRow(
      String tenantId,
      String boundaryId,
      String agentDefinitionId,
      String lifecycleStatus,
      int boundaryVersion,
      List<ToolGrantRow> grants,
      int grantCount,
      boolean grantsReadSkill,
      boolean grantsReadReference,
      boolean containsApprovalRequiredGrant,
      String checksum,
      Instant createdAt,
      Instant updatedAt) {
    static ToolBoundaryRow from(ToolPermissionBoundary boundary) {
      var grants = boundary.allowedToolGrants().stream().map(grant -> ToolGrantRow.from(boundary, grant)).toList();
      return new ToolBoundaryRow(
          boundary.tenantId(),
          boundary.boundaryId(),
          boundary.agentDefinitionId(),
          boundary.status().name(),
          boundary.boundaryVersion(),
          grants,
          grants.size(),
          boundary.allowedToolGrants().stream().anyMatch(grant -> grant.category() == ToolPermissionBoundary.Category.READ_SKILL),
          boundary.allowedToolGrants().stream().anyMatch(grant -> grant.category() == ToolPermissionBoundary.Category.READ_REFERENCE),
          boundary.allowedToolGrants().stream().anyMatch(grant -> grant.autonomy() != null && grant.autonomy().equalsIgnoreCase("approval_required")),
          boundary.checksum(),
          boundary.createdAt(),
          boundary.updatedAt());
    }
  }

  public record ToolGrantRow(
      String tenantId,
      String boundaryId,
      String agentDefinitionId,
      int boundaryVersion,
      String toolId,
      String category,
      String capabilityId,
      List<String> allowedOperations,
      List<String> allowedModes,
      String sideEffectLevel,
      String autonomy,
      boolean idempotencyRequired,
      String traceLevel) {
    static ToolGrantRow from(ToolPermissionBoundary boundary, ToolPermissionBoundary.ToolGrant grant) {
      return new ToolGrantRow(
          boundary.tenantId(),
          boundary.boundaryId(),
          boundary.agentDefinitionId(),
          boundary.boundaryVersion(),
          grant.toolId(),
          grant.category().name().toLowerCase(Locale.ROOT),
          grant.capabilityId(),
          grant.allowedOperations(),
          grant.allowedModes(),
          grant.sideEffectLevel(),
          grant.autonomy(),
          grant.idempotencyRequired(),
          grant.traceLevel());
    }
  }

  public record BoundaryDetailQuery(String tenantId, String boundaryId) {}
  public record ActiveBoundaryQuery(String tenantId, String boundaryId, String lifecycleStatus) {
    public static ActiveBoundaryQuery active(String tenantId, String boundaryId) {
      return new ActiveBoundaryQuery(tenantId, boundaryId, "ACTIVE");
    }
  }
  public record AgentBoundaryQuery(String tenantId, String agentDefinitionId) {}
  public record ReadSkillBoundaryQuery(String tenantId, boolean grantsReadSkill) {
    public static ReadSkillBoundaryQuery granted(String tenantId) {
      return new ReadSkillBoundaryQuery(tenantId, true);
    }
  }
  public record ReadReferenceBoundaryQuery(String tenantId, boolean grantsReadReference) {
    public static ReadReferenceBoundaryQuery granted(String tenantId) {
      return new ReadReferenceBoundaryQuery(tenantId, true);
    }
  }
  public record BoundaryRows(List<ToolBoundaryRow> boundaries) {}

  @Consume.FromEventSourcedEntity(ToolPermissionBoundaryEntity.class)
  public static class ToolBoundariesUpdater extends TableUpdater<ToolBoundaryRow> {
    public Effect<ToolBoundaryRow> onEvent(ToolPermissionBoundaryEntity.Event event) {
      return effects().updateRow(ToolBoundaryRow.from(event.boundary()));
    }
  }

  @Query("""
      SELECT *
      FROM tool_boundary_grant_view
      WHERE tenantId = :tenantId AND boundaryId = :boundaryId
      """)
  public QueryEffect<ToolBoundaryRow> getDetail(BoundaryDetailQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT *
      FROM tool_boundary_grant_view
      WHERE tenantId = :tenantId AND boundaryId = :boundaryId AND lifecycleStatus = :lifecycleStatus
      """)
  public QueryEffect<ToolBoundaryRow> activeRuntimeLookup(ActiveBoundaryQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT * AS boundaries
      FROM tool_boundary_grant_view
      WHERE tenantId = :tenantId AND agentDefinitionId = :agentDefinitionId
      """)
  public QueryEffect<BoundaryRows> byAgent(AgentBoundaryQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT * AS boundaries
      FROM tool_boundary_grant_view
      WHERE tenantId = :tenantId AND grantsReadSkill = :grantsReadSkill
      """)
  public QueryEffect<BoundaryRows> withReadSkillGrant(ReadSkillBoundaryQuery query) {
    return queryResult();
  }

  @Query("""
      SELECT * AS boundaries
      FROM tool_boundary_grant_view
      WHERE tenantId = :tenantId AND grantsReadReference = :grantsReadReference
      """)
  public QueryEffect<BoundaryRows> withReadReferenceGrant(ReadReferenceBoundaryQuery query) {
    return queryResult();
  }
}
