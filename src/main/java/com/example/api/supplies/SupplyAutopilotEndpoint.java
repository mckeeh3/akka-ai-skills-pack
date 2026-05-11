package com.example.api.supplies;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.CommandException;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;
import com.example.application.supplies.PendingSupplyDecisionView;
import com.example.application.supplies.SupplyAutopilotWorkflow;
import com.example.application.supplies.SupplyDecisionEntity;
import com.example.application.supplies.SupplyRiskView;
import com.example.application.supplies.SupplyTraceView;
import com.example.domain.supplies.Supply;
import com.example.domain.supplies.Supply.DeviceLifecycleStatus;
import com.example.domain.supplies.Supply.DeviceTelemetry;
import com.example.domain.supplies.Supply.SupplyDecisionCard;
import com.example.domain.supplies.Supply.SupplyEvidence;
import com.example.domain.supplies.Supply.SupplyPolicyClauseRef;
import com.example.domain.supplies.Supply.TraceRef;
import com.example.domain.supplies.SupplyDecision;
import java.time.Instant;
import java.util.List;

/** Browser/API-facing control and audit surface for the supplies autopilot reference slice. */
@HttpEndpoint("/api/supplies")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class SupplyAutopilotEndpoint extends AbstractHttpEndpoint {

  public record TelemetryRequest(
      String workflowId,
      String idempotencyKey,
      String traceId,
      String correlationId,
      String customerId,
      String deviceId,
      Instant observedAt,
      int tonerPercent,
      int pagesSinceLastSupply,
      String lifecycleStatus) {}

  public record DecisionActionRequest(String idempotencyKey, String actor, String rationale) {}

  public record WorkflowResponse(
      String workflowId,
      String decisionId,
      String status,
      boolean staleDecisionTimerRequested,
      boolean shipmentPrepared,
      String message,
      String traceId,
      String outcomeId) {}

  public record RiskRowsResponse(List<RiskRowResponse> risks) {}

  public record RiskRowResponse(
      String workflowId,
      String decisionId,
      String customerId,
      String deviceId,
      String status,
      int tonerPercent,
      double depletionRisk,
      String proposedAction,
      boolean staleDecisionTimerRequested,
      boolean shipmentPrepared,
      String traceId,
      String outcomeId) {}

  public record DecisionRowsResponse(List<DecisionRowResponse> decisions) {}

  public record DecisionRowResponse(
      String decisionId,
      String status,
      String proposedAction,
      String riskSummary,
      String impactSummary,
      double confidence,
      long estimatedCostCents,
      int evidenceCount,
      int policyClauseCount,
      String traceId,
      String outcomeId) {}

  public record DecisionDetailResponse(
      String decisionId,
      String status,
      String proposedAction,
      String objectiveId,
      String recommendedSku,
      long estimatedCostCents,
      double depletionRisk,
      double confidence,
      String riskSummary,
      String impactSummary,
      List<EvidenceResponse> evidence,
      List<PolicyClauseResponse> policyClauses,
      List<String> alternatives,
      String traceId,
      String correlationId,
      String outcomeId,
      List<TraceEventResponse> traceEvents) {}

  public record EvidenceResponse(String type, String source, String summary, double confidence, String traceId) {}

  public record PolicyClauseResponse(String clauseId, String summary) {}

  public record TraceEventResponse(
      String eventId,
      String type,
      String idempotencyKey,
      Instant occurredAt,
      String actor,
      String summary,
      int policyClauseCount,
      String outcomeId) {}

  public record TraceLookupResponse(
      String decisionId,
      String status,
      String traceId,
      String correlationId,
      String outcomeId,
      List<TraceEventResponse> events) {}

  public record StatusResponse(String status) {}

  private final ComponentClient componentClient;

  public SupplyAutopilotEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post("/telemetry")
  public HttpResponse submitTelemetry(TelemetryRequest request) {
    try {
      var telemetry = toTelemetry(request);
      var state =
          componentClient
              .forWorkflow(request.workflowId())
              .method(SupplyAutopilotWorkflow::start)
              .invoke(telemetry);
      return HttpResponses.created(toWorkflowResponse(state));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    } catch (IllegalArgumentException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Get("/risks")
  public HttpResponse risks() {
    var status = requestContext().queryParams().getString("status").orElse("WAITING_FOR_APPROVAL");
    var minDecisionId = requestContext().queryParams().getString("minDecisionId").orElse("");
    var result =
        componentClient
            .forView()
            .method(SupplyRiskView::getByStatus)
            .invoke(new SupplyRiskView.FindByStatus(status, minDecisionId));
    return HttpResponses.ok(new RiskRowsResponse(result.risks().stream().map(SupplyAutopilotEndpoint::toRiskRow).toList()));
  }

  @Get("/decisions/pending")
  public HttpResponse pendingDecisions() {
    var minDecisionId = requestContext().queryParams().getString("minDecisionId").orElse("");
    var result =
        componentClient
            .forView()
            .method(PendingSupplyDecisionView::getByStatus)
            .invoke(new PendingSupplyDecisionView.FindByStatus(SupplyDecision.Status.APPROVAL_REQUIRED.name(), minDecisionId));
    return HttpResponses.ok(new DecisionRowsResponse(result.decisions().stream().map(SupplyAutopilotEndpoint::toDecisionRow).toList()));
  }

  @Get("/decisions/{decisionId}")
  public HttpResponse decision(String decisionId) {
    try {
      var state = getDecisionState(decisionId);
      if (!state.exists() || state.card() == null) {
        return HttpResponses.notFound("decision not found");
      }
      return HttpResponses.ok(toDecisionDetail(state));
    } catch (CommandException error) {
      return HttpResponses.notFound(error.getMessage());
    }
  }

  @Post("/decisions/{decisionId}/approve")
  public HttpResponse approve(String decisionId, DecisionActionRequest request) {
    return applyDecisionAction(
        decisionId,
        request,
        action ->
            componentClient
                .forWorkflow(decisionId)
                .method(SupplyAutopilotWorkflow::approve)
                .invoke(new SupplyAutopilotWorkflow.ApproveDecision(action.idempotencyKey(), actor(action), action.rationale())));
  }

  @Post("/decisions/{decisionId}/reject")
  public HttpResponse reject(String decisionId, DecisionActionRequest request) {
    return applyDecisionAction(
        decisionId,
        request,
        action ->
            componentClient
                .forWorkflow(decisionId)
                .method(SupplyAutopilotWorkflow::reject)
                .invoke(new SupplyAutopilotWorkflow.RejectDecision(action.idempotencyKey(), actor(action), action.rationale())));
  }

  @Post("/decisions/{decisionId}/suppress")
  public HttpResponse suppress(String decisionId, DecisionActionRequest request) {
    return applyDecisionAction(
        decisionId,
        request,
        action ->
            componentClient
                .forWorkflow(decisionId)
                .method(SupplyAutopilotWorkflow::suppress)
                .invoke(new SupplyAutopilotWorkflow.SuppressDecision(action.idempotencyKey(), actor(action), action.rationale())));
  }

  @Get("/traces/{traceId}")
  public HttpResponse trace(String traceId) {
    var result =
        componentClient
            .forView()
            .method(SupplyTraceView::getByTraceId)
            .invoke(new SupplyTraceView.FindByTraceId(traceId));
    if (result.traces().isEmpty()) {
      return HttpResponses.notFound("trace not found");
    }
    return HttpResponses.ok(toTraceLookup(result.traces().getFirst()));
  }

  private HttpResponse applyDecisionAction(
      String decisionId,
      DecisionActionRequest request,
      java.util.function.Function<DecisionActionRequest, SupplyAutopilotWorkflow.State> invoke) {
    try {
      validateDecisionActionRequest(request);
      var state = invoke.apply(request);
      return HttpResponses.ok(toWorkflowResponse(state));
    } catch (CommandException error) {
      if (error.getMessage().contains("not started") || error.getMessage().contains("not open")) {
        return HttpResponses.notFound(error.getMessage());
      }
      return HttpResponses.badRequest(error.getMessage());
    } catch (IllegalArgumentException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  private SupplyDecision.State getDecisionState(String decisionId) {
    return componentClient
        .forEventSourcedEntity(decisionId)
        .method(SupplyDecisionEntity::getDecision)
        .invoke();
  }

  private DeviceTelemetry toTelemetry(TelemetryRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("telemetry request is required");
    }
    Supply.requireNonBlank(request.workflowId(), "workflowId");
    return new DeviceTelemetry(
        request.idempotencyKey(),
        new TraceRef(request.traceId(), request.correlationId()),
        request.customerId(),
        request.deviceId(),
        request.observedAt(),
        request.tonerPercent(),
        request.pagesSinceLastSupply(),
        parseLifecycleStatus(request.lifecycleStatus()));
  }

  private void validateDecisionActionRequest(DecisionActionRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("decision action request is required");
    }
    Supply.requireIdempotencyKey(request.idempotencyKey());
    Supply.requireNonBlank(actor(request), "actor");
    Supply.requireNonBlank(request.rationale(), "rationale");
  }

  private String actor(DecisionActionRequest request) {
    if (request.actor() != null && !request.actor().isBlank()) {
      return request.actor();
    }
    return requestContext().requestHeader("X-Actor").map(akka.http.javadsl.model.HttpHeader::value).orElse("");
  }

  private static DeviceLifecycleStatus parseLifecycleStatus(String lifecycleStatus) {
    Supply.requireNonBlank(lifecycleStatus, "lifecycleStatus");
    return DeviceLifecycleStatus.valueOf(lifecycleStatus);
  }

  private static RiskRowResponse toRiskRow(SupplyRiskView.RiskRow row) {
    return new RiskRowResponse(
        row.workflowId(),
        row.decisionId(),
        row.customerId(),
        row.deviceId(),
        row.status(),
        row.tonerPercent(),
        row.depletionRisk(),
        row.proposedAction(),
        row.staleDecisionTimerRequested(),
        row.shipmentPrepared(),
        row.traceId(),
        row.outcomeId());
  }

  private static DecisionRowResponse toDecisionRow(PendingSupplyDecisionView.DecisionRow row) {
    return new DecisionRowResponse(
        row.decisionId(),
        row.status(),
        row.proposedAction(),
        row.riskSummary(),
        row.impactSummary(),
        row.confidence(),
        row.estimatedCostCents(),
        row.evidenceCount(),
        row.policyClauseCount(),
        row.traceId(),
        row.outcomeId());
  }

  private static WorkflowResponse toWorkflowResponse(SupplyAutopilotWorkflow.State state) {
    var card = state.decisionCard();
    return new WorkflowResponse(
        state.workflowId(),
        state.decisionId(),
        state.status().name(),
        state.staleDecisionTimerRequested(),
        state.shipmentPrepared(),
        state.message(),
        state.telemetry().trace().traceId(),
        card == null ? "" : card.outcome().outcomeId());
  }

  private static DecisionDetailResponse toDecisionDetail(SupplyDecision.State state) {
    var card = state.card();
    var recommendation = card.recommendation();
    return new DecisionDetailResponse(
        state.decisionId(),
        state.status().name(),
        card.proposedAction().name(),
        card.objective().goalId(),
        recommendation.item().sku(),
        recommendation.estimatedCostCents(),
        recommendation.depletionRisk(),
        recommendation.confidence(),
        card.riskSummary(),
        card.impactSummary(),
        recommendation.evidence().stream().map(SupplyAutopilotEndpoint::toEvidence).toList(),
        recommendation.policyClauses().stream().map(SupplyAutopilotEndpoint::toPolicyClause).toList(),
        recommendation.alternatives(),
        card.trace().traceId(),
        card.trace().correlationId(),
        card.outcome().outcomeId(),
        state.traceEvents().stream().map(SupplyAutopilotEndpoint::toTraceEvent).toList());
  }

  private static EvidenceResponse toEvidence(SupplyEvidence evidence) {
    return new EvidenceResponse(
        evidence.type().name(),
        evidence.source(),
        evidence.summary(),
        evidence.confidence(),
        evidence.trace().traceId());
  }

  private static PolicyClauseResponse toPolicyClause(SupplyPolicyClauseRef clause) {
    return new PolicyClauseResponse(clause.clauseId(), clause.summary());
  }

  private static TraceLookupResponse toTraceLookup(SupplyTraceView.TraceRow trace) {
    return new TraceLookupResponse(
        trace.decisionId(),
        trace.status(),
        trace.traceId(),
        trace.correlationId(),
        trace.outcomeId(),
        trace.events().stream()
            .map(
                event ->
                    new TraceEventResponse(
                        event.eventId(),
                        event.type(),
                        event.idempotencyKey(),
                        event.occurredAt(),
                        event.actor(),
                        event.summary(),
                        event.policyClauseCount(),
                        event.outcomeId()))
            .toList());
  }

  private static TraceEventResponse toTraceEvent(Supply.SupplyTraceEvent event) {
    return new TraceEventResponse(
        event.eventId(),
        event.type().name(),
        event.idempotencyKey(),
        event.occurredAt(),
        event.actor(),
        event.summary(),
        event.policyClauses().size(),
        event.outcome() == null ? "" : event.outcome().outcomeId());
  }
}
