package com.example.application;

import akka.javasdk.Metadata;
import akka.javasdk.agent.SessionMemoryEntity;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Produce;
import akka.javasdk.consumer.Consumer;

/** Topic-producing consumer that emits audit events after session memory compaction completes. */
@Component(id = "session-memory-compaction-audit-consumer")
@Consume.FromEventSourcedEntity(SessionMemoryEntity.class)
@Produce.ToTopic("session-memory-compactions")
public class SessionMemoryCompactionAuditConsumer extends Consumer {

  public Effect onEvent(SessionMemoryEntity.Event event) {
    return switch (event) {
      case SessionMemoryEntity.Event.AiMessageAdded aiMessage
          when SessionMemoryCompactionAgent.COMPONENT_ID.equals(aiMessage.componentId()) -> {
        var sessionId = messageContext().eventSubject().orElseThrow();
        var metadata = Metadata.EMPTY.add("ce-subject", sessionId);
        yield effects()
            .produce(
                new SessionMemoryCompactionAudit(
                    sessionId,
                    aiMessage.componentId(),
                    aiMessage.historySizeInBytes(),
                    "session memory compacted"),
                metadata);
      }
      default -> effects().ignore();
    };
  }
}
