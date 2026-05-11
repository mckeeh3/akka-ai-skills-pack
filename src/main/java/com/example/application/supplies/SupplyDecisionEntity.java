package com.example.application.supplies;

import akka.javasdk.annotations.Component;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import akka.javasdk.eventsourcedentity.EventSourcedEntityContext;
import com.example.domain.supplies.SupplyDecision;
import com.example.domain.supplies.SupplyDecisionCommandHandler;
import com.example.domain.supplies.SupplyDecisionEventHandler;
import com.example.domain.supplies.SupplyDecisionValidator;
import java.util.List;

/** Audit-grade event-sourced write model for supply recommendation and decision facts. */
@Component(id = "supply-decision")
public class SupplyDecisionEntity
    extends EventSourcedEntity<SupplyDecision.State, SupplyDecision.Event> {

  private final String decisionId;

  public SupplyDecisionEntity(EventSourcedEntityContext context) {
    this.decisionId = context.entityId();
  }

  @Override
  public SupplyDecision.State emptyState() {
    return SupplyDecision.State.empty(decisionId);
  }

  public ReadOnlyEffect<SupplyDecision.State> getDecision() {
    return effects().reply(currentState());
  }

  public Effect<SupplyDecision.State> openRecommendation(SupplyDecision.Command.OpenRecommendation command) {
    var errors = SupplyDecisionValidator.validate(currentState(), command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    return persistAndReply(SupplyDecisionCommandHandler.onCommand(currentState(), command));
  }

  public Effect<SupplyDecision.State> requireApproval(SupplyDecision.Command.RequireApproval command) {
    var errors = SupplyDecisionValidator.validate(currentState(), command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    return persistAndReply(SupplyDecisionCommandHandler.onCommand(currentState(), command));
  }

  public Effect<SupplyDecision.State> approve(SupplyDecision.Command.Approve command) {
    var errors = SupplyDecisionValidator.validate(currentState(), command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    return persistAndReply(SupplyDecisionCommandHandler.onCommand(currentState(), command));
  }

  public Effect<SupplyDecision.State> reject(SupplyDecision.Command.Reject command) {
    var errors = SupplyDecisionValidator.validate(currentState(), command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    return persistAndReply(SupplyDecisionCommandHandler.onCommand(currentState(), command));
  }

  public Effect<SupplyDecision.State> suppress(SupplyDecision.Command.Suppress command) {
    var errors = SupplyDecisionValidator.validate(currentState(), command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    return persistAndReply(SupplyDecisionCommandHandler.onCommand(currentState(), command));
  }

  public Effect<SupplyDecision.State> prepareShipment(SupplyDecision.Command.PrepareShipment command) {
    var errors = SupplyDecisionValidator.validate(currentState(), command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    return persistAndReply(SupplyDecisionCommandHandler.onCommand(currentState(), command));
  }

  public Effect<SupplyDecision.State> escalateStale(SupplyDecision.Command.EscalateStale command) {
    var errors = SupplyDecisionValidator.validate(currentState(), command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    return persistAndReply(SupplyDecisionCommandHandler.onCommand(currentState(), command));
  }

  public Effect<SupplyDecision.State> linkOutcome(SupplyDecision.Command.LinkOutcome command) {
    var errors = SupplyDecisionValidator.validate(currentState(), command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    return persistAndReply(SupplyDecisionCommandHandler.onCommand(currentState(), command));
  }

  @Override
  public SupplyDecision.State applyEvent(SupplyDecision.Event event) {
    return SupplyDecisionEventHandler.apply(currentState(), event);
  }

  private Effect<SupplyDecision.State> persistAndReply(List<SupplyDecision.Event> events) {
    if (events.isEmpty()) {
      return effects().reply(currentState());
    }
    if (events.size() == 1) {
      return effects().persist(events.getFirst()).thenReply(__ -> currentState());
    }
    return effects().persistAll(events).thenReply(__ -> currentState());
  }
}
