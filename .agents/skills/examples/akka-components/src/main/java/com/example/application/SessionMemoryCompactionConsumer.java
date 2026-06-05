package com.example.application;

import akka.javasdk.agent.SessionMemoryEntity;
import akka.javasdk.agent.SessionMessage;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.consumer.Consumer;
import java.time.Instant;

/** Consumer example that compacts oversized built-in agent session memory histories. */
@Component(id = "session-memory-compaction-consumer")
@Consume.FromEventSourcedEntity(SessionMemoryEntity.class)
public class SessionMemoryCompactionConsumer extends Consumer {

  private static final long COMPACTION_THRESHOLD_BYTES = 500L;

  private final ComponentClient componentClient;

  public SessionMemoryCompactionConsumer(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  public Effect onEvent(SessionMemoryEntity.Event event) {
    return switch (event) {
      case SessionMemoryEntity.Event.AiMessageAdded aiMessage
          when shouldCompact(aiMessage) -> onOversizedHistory();
      default -> effects().ignore();
    };
  }

  private boolean shouldCompact(SessionMemoryEntity.Event.AiMessageAdded aiMessage) {
    return aiMessage.historySizeInBytes() > COMPACTION_THRESHOLD_BYTES
        && !SessionMemoryCompactionAgent.COMPONENT_ID.equals(aiMessage.componentId());
  }

  private Effect onOversizedHistory() {
    var sessionId = messageContext().eventSubject().orElseThrow();
    var history =
        componentClient
            .forEventSourcedEntity(sessionId)
            .method(SessionMemoryEntity::getHistory)
            .invoke(new SessionMemoryEntity.GetHistoryCmd());

    var summary =
        componentClient
            .forAgent()
            .inSession(sessionId)
            .method(SessionMemoryCompactionAgent::summarizeSessionHistory)
            .invoke(history);

    var now = Instant.now();
    componentClient
        .forEventSourcedEntity(sessionId)
        .method(SessionMemoryEntity::compactHistory)
        .invoke(
            new SessionMemoryEntity.CompactionCmd(
                new SessionMessage.UserMessage(
                    now, summary.userMessage(), SessionMemoryCompactionAgent.COMPONENT_ID),
                new SessionMessage.AiMessage(
                    now, summary.aiMessage(), SessionMemoryCompactionAgent.COMPONENT_ID),
                history.sequenceNumber()));

    return effects().done();
  }
}
