# Agent runtime state reference

Small, agent-oriented reference for Akka built-in agent runtime state.

Primary official semantics:
- `akka-context/sdk/agents/prompt.html.md`
- `akka-context/sdk/agents/memory.html.md`
- `akka-context/sdk/agents/testing.html.md`
- `akka-context/sdk/views.html.md`
- `akka-context/sdk/consuming-producing.html.md`

Local executable examples:
- `src/main/java/com/example/application/TemplateBackedActivityAgent.java`
- `src/main/java/com/example/api/ActivityPromptEndpoint.java`
- `src/main/java/com/example/application/PromptTemplateHistoryView.java`
- `src/main/java/com/example/api/PromptTemplateHistoryEndpoint.java`
- `src/main/java/com/example/application/SessionMemoryByComponentView.java`
- `src/main/java/com/example/api/SessionMemoryViewEndpoint.java`
- `src/main/java/com/example/application/SessionMemoryAlertView.java`
- `src/main/java/com/example/api/SessionMemoryAlertStreamEndpoint.java`
- `src/main/java/com/example/application/SessionMemoryAlertsConsumer.java`
- `src/main/java/com/example/application/SessionMemoryCompactionAgent.java`
- `src/main/java/com/example/application/SessionMemoryCompactionConsumer.java`
- `src/main/java/com/example/application/SessionMemoryCompactionAuditConsumer.java`
- `src/main/java/com/example/application/SessionMemoryCompactionAuditView.java`
- `src/main/java/com/example/api/SessionMemoryCompactionStreamEndpoint.java`

## Quick selection

Use these patterns when:

- prompt text should change at runtime without redeploying
- current prompt-template state should be queryable
- session memory should drive analytics or threshold alerts
- long session history should be compacted into a short summary
- compaction outcomes should be published or streamed

## Minimal patterns

### 1. Agent loading its system prompt from `PromptTemplate`

```java
@Component(id = "template-backed-activity-agent")
public class TemplateBackedActivityAgent extends Agent {
  public static final String PROMPT_TEMPLATE_ID = "activity-agent-prompt";

  public Effect<String> suggest(String message) {
    return effects()
        .systemMessageFromTemplate(PROMPT_TEMPLATE_ID)
        .userMessage(message)
        .thenReply();
  }
}
```

Reference:
- `src/main/java/com/example/application/TemplateBackedActivityAgent.java`

### 2. HTTP management of prompt templates

```java
componentClient
    .forEventSourcedEntity(TemplateBackedActivityAgent.PROMPT_TEMPLATE_ID)
    .method(PromptTemplate::update)
    .invoke(request.prompt());
```

Reference:
- `src/main/java/com/example/api/ActivityPromptEndpoint.java`

### 3. View over current prompt-template state

```java
@Component(id = "prompt-template-history")
public class PromptTemplateHistoryView extends View {

  @Consume.FromEventSourcedEntity(PromptTemplate.class)
  public static class PromptTemplateHistoryUpdater extends TableUpdater<PromptTemplateHistoryRow> {
    public Effect<PromptTemplateHistoryRow> onEvent(PromptTemplate.Event event) {
      // keep current prompt and update count per template id
    }
  }
}
```

References:
- `src/main/java/com/example/application/PromptTemplateHistoryView.java`
- `src/main/java/com/example/api/PromptTemplateHistoryEndpoint.java`

### 4. View over `SessionMemoryEntity`

```java
@Component(id = "session-memory-by-component")
public class SessionMemoryByComponentView extends View {

  @Consume.FromEventSourcedEntity(SessionMemoryEntity.class)
  public static class SessionMemoryUpdater extends TableUpdater<SessionRow> {
    public Effect<SessionRow> onEvent(SessionMemoryEntity.Event event) {
      // track last component, message count, and history size
    }
  }
}
```

References:
- `src/main/java/com/example/application/SessionMemoryByComponentView.java`
- `src/main/java/com/example/api/SessionMemoryViewEndpoint.java`

### 5. Threshold alert view + SSE endpoint

```java
@Component(id = "session-memory-alert-view")
public class SessionMemoryAlertView extends View {
  @Consume.FromEventSourcedEntity(SessionMemoryEntity.class)
  public static class SessionMemoryAlertUpdater extends TableUpdater<AlertRow> {}
}
```

```java
@Get("/alerts/{componentId}")
public HttpResponse alerts(String componentId) {
  var source = componentClient
      .forView()
      .stream(SessionMemoryAlertView::continuousByComponent)
      .entriesSource(new SessionMemoryAlertView.FindByComponent(componentId),
          requestContext().lastSeenSseEventId().map(Instant::parse));
  return HttpResponses.serverSentEventsForView(source);
}
```

References:
- `src/main/java/com/example/application/SessionMemoryAlertView.java`
- `src/main/java/com/example/api/SessionMemoryAlertStreamEndpoint.java`

### 6. Threshold alert consumer over session memory

```java
@Component(id = "session-memory-alerts-consumer")
@Consume.FromEventSourcedEntity(SessionMemoryEntity.class)
@Produce.ToTopic("session-memory-alerts")
public class SessionMemoryAlertsConsumer extends Consumer {}
```

Reference:
- `src/main/java/com/example/application/SessionMemoryAlertsConsumer.java`

### 7. Compaction flow

```java
var history = componentClient
    .forEventSourcedEntity(sessionId)
    .method(SessionMemoryEntity::getHistory)
    .invoke(new SessionMemoryEntity.GetHistoryCmd());

var summary = componentClient
    .forAgent()
    .inSession(sessionId)
    .method(SessionMemoryCompactionAgent::summarizeSessionHistory)
    .invoke(history);

componentClient
    .forEventSourcedEntity(sessionId)
    .method(SessionMemoryEntity::compactHistory)
    .invoke(new SessionMemoryEntity.CompactionCmd(...));
```

References:
- `src/main/java/com/example/application/SessionMemoryCompactionAgent.java`
- `src/main/java/com/example/application/SessionMemoryCompactionConsumer.java`

### 8. Topic audit after compaction

```java
@Component(id = "session-memory-compaction-audit-consumer")
@Consume.FromEventSourcedEntity(SessionMemoryEntity.class)
@Produce.ToTopic("session-memory-compactions")
public class SessionMemoryCompactionAuditConsumer extends Consumer {}
```

References:
- `src/main/java/com/example/application/SessionMemoryCompactionAuditConsumer.java`
- `src/main/java/com/example/application/SessionMemoryCompactionAuditView.java`
- `src/main/java/com/example/api/SessionMemoryCompactionStreamEndpoint.java`

## Testing reminders

Prefer:
- `TestModelProvider` for compaction-agent tests and end-to-end compaction flows
- `withEventSourcedEntityIncomingMessages(SessionMemoryEntity.class)` for session-memory views
- `withTopicOutgoingMessages("session-memory-compactions")` for audit-topic assertions
- `withTopicIncomingMessages("session-memory-compactions")` only when you intentionally test topic-backed runtime-state views

Reference tests:
- `src/test/java/com/example/application/ActivityPromptEndpointIntegrationTest.java`
- `src/test/java/com/example/application/PromptTemplateHistoryViewIntegrationTest.java`
- `src/test/java/com/example/application/PromptTemplateHistoryEndpointIntegrationTest.java`
- `src/test/java/com/example/application/SessionMemoryViewEndpointIntegrationTest.java`
- `src/test/java/com/example/application/SessionMemoryAlertStreamEndpointIntegrationTest.java`
- `src/test/java/com/example/application/SessionMemoryCompactionConsumerIntegrationTest.java`
- `src/test/java/com/example/application/SessionMemoryCompactionAuditConsumerIntegrationTest.java`
