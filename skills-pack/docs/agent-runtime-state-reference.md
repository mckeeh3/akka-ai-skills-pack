# Agent runtime state reference

Small, agent-oriented reference for Akka built-in agent runtime state.

Primary official semantics:
- `akka-context/sdk/agents/prompt.html.md`
- `akka-context/sdk/agents/memory.html.md`
- `akka-context/sdk/agents/testing.html.md`
- `akka-context/sdk/views.html.md`
- `akka-context/sdk/consuming-producing.html.md`

Current local executable agent example:
- `../examples/akka-components/src/main/java/ai/first/application/foundation/agent/WorkstreamRuntimeAgent.java`

The built-in `PromptTemplate` and `SessionMemoryEntity` snippets below are patterns, not current curated core-app classes. The current core app primarily uses governed prompt/skill/reference documents plus durable runtime traces.

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
@Component(id = "template-backed-domain-agent")
public class DomainTemplateBackedAgent extends Agent {
  public static final String PROMPT_TEMPLATE_ID = "domain-agent-prompt";

  public Effect<String> suggest(String message) {
    return effects()
        .systemMessageFromTemplate(PROMPT_TEMPLATE_ID)
        .userMessage(message)
        .thenReply();
  }
}
```

Reference:
- `../examples/akka-components/src/main/java/ai/first/application/foundation/agent/WorkstreamRuntimeAgent.java`

### 2. HTTP management of prompt templates

```java
componentClient
    .forEventSourcedEntity(DomainTemplateBackedAgent.PROMPT_TEMPLATE_ID)
    .method(PromptTemplate::update)
    .invoke(request.prompt());
```

Reference:

### 3. View over current prompt-template state

```java
@Component(id = "domain-prompt-template-history")
public class DomainPromptTemplateProjection extends View {

  @Consume.FromEventSourcedEntity(PromptTemplate.class)
  public static class PromptTemplateHistoryUpdater extends TableUpdater<PromptTemplateHistoryRow> {
    public Effect<PromptTemplateHistoryRow> onEvent(PromptTemplate.Event event) {
      // keep current prompt and update count per template id
    }
  }
}
```

References:

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

### 6. Threshold alert consumer over session memory

```java
@Component(id = "session-memory-alerts-consumer")
@Consume.FromEventSourcedEntity(SessionMemoryEntity.class)
@Produce.ToTopic("session-memory-alerts")
public class SessionMemoryAlertsConsumer extends Consumer {}
```

Reference:

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

### 8. Topic audit after compaction

```java
@Component(id = "session-memory-compaction-audit-consumer")
@Consume.FromEventSourcedEntity(SessionMemoryEntity.class)
@Produce.ToTopic("session-memory-compactions")
public class SessionMemoryCompactionAuditConsumer extends Consumer {}
```

References:

## Testing reminders

Prefer:
- `TestModelProvider` for compaction-agent tests and end-to-end compaction flows
- `withEventSourcedEntityIncomingMessages(SessionMemoryEntity.class)` for session-memory views
- `withTopicOutgoingMessages("session-memory-compactions")` for audit-topic assertions
- `withTopicIncomingMessages("session-memory-compactions")` only when you intentionally test topic-backed runtime-state views

Reference tests:
