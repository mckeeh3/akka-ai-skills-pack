package com.example.application;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import akka.javasdk.keyvalueentity.KeyValueEntityContext;
import com.example.domain.ExpiringDraftCartSession;
import java.time.Duration;

/**
 * Focused TTL example for key value entities.
 *
 * <p>Each successful state update attaches a 30 day time-to-live. If no further updates are made
 * before that duration elapses, Akka automatically deletes the entity.
 */
@Component(id = "expiring-draft-cart-session")
public class ExpiringDraftCartSessionEntity
    extends KeyValueEntity<ExpiringDraftCartSession.State> {

  static final Duration TTL = Duration.ofDays(30);

  private final String sessionId;

  public ExpiringDraftCartSessionEntity(KeyValueEntityContext context) {
    this.sessionId = context.entityId();
  }

  @Override
  public ExpiringDraftCartSession.State emptyState() {
    return ExpiringDraftCartSession.State.empty(sessionId);
  }

  public ReadOnlyEffect<ExpiringDraftCartSession.State> getSession() {
    return effects().reply(currentState());
  }

  public Effect<Done> addItem(String productId) {
    if (productId == null || productId.isBlank()) {
      return effects().error("productId must not be blank.");
    }

    var newState = currentState().addItem(productId);
    return effects().updateState(newState).expireAfter(TTL).thenReply(Done.getInstance());
  }
}
