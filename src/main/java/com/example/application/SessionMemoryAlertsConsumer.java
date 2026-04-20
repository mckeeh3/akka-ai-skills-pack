package com.example.application;

import akka.javasdk.Metadata;
import akka.javasdk.agent.SessionMemoryEntity;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Produce;
import akka.javasdk.consumer.Consumer;

/** Consumer example that reacts to session memory growth and emits alerts to a topic. */
@Component(id = "session-memory-alerts-consumer")
@Consume.FromEventSourcedEntity(SessionMemoryEntity.class)
@Produce.ToTopic("session-memory-alerts")
public class SessionMemoryAlertsConsumer extends Consumer {

  private static final long ALERT_THRESHOLD_BYTES = 500L;

  public record SessionMemoryAlert(
      String sessionId, String componentId, long historySizeInBytes, String reason) {}

  public Effect onEvent(SessionMemoryEntity.Event event) {
    return switch (event) {
      case SessionMemoryEntity.Event.AiMessageAdded aiMessage
          when aiMessage.historySizeInBytes() > ALERT_THRESHOLD_BYTES -> {
        var sessionId = messageContext().eventSubject().orElseThrow();
        var metadata = Metadata.EMPTY.add("ce-subject", sessionId);
        yield effects()
            .produce(
                new SessionMemoryAlert(
                    sessionId,
                    aiMessage.componentId(),
                    aiMessage.historySizeInBytes(),
                    "session memory exceeded threshold"),
                metadata);
      }
      default -> effects().ignore();
    };
  }
}
