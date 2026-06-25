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

The current SaaS Foundation App primarily uses governed prompt/skill/reference documents, `ToolPermissionBoundary`, runtime tool registration, and durable runtime traces. Its curated `WorkstreamRuntimeAgent` is the managed invocation example; it does **not** demonstrate built-in `PromptTemplate` or `SessionMemoryEntity` state. Treat the snippets below as target-project patterns to use only when an app intentionally adopts Akka built-in prompt templates or session memory in addition to governed behavior records.

Built-in prompt templates and session memory are runtime context/state helpers. They do not grant authorization, expand tenant/customer scope, replace governed prompt/skill/reference records, or substitute for provider/security fail-closed behavior. Protected management views and update APIs still need `AuthContext`, tenant/customer checks, redaction, audit/work traces, and tests.

## Quick selection

Use these target-project patterns when:

- prompt text should change at runtime through an explicitly governed built-in template path;
- current prompt-template state should be queryable by authorized admins;
- session memory should drive analytics or threshold alerts;
- long session history should be compacted into a short summary;
- compaction outcomes should be published or streamed.

For generated AI-first SaaS foundation workstream agents, prefer `./agent-runtime-invocation-pattern.md` as the default managed-agent path unless the accepted current intent explicitly selects built-in prompt templates or session memory.

## Minimal target-project patterns

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

### 2. Protected management of prompt templates

```java
// Target-project pattern: authenticate and authorize before this command.
componentClient
    .forEventSourcedEntity(DomainTemplateBackedAgent.PROMPT_TEMPLATE_ID)
    .method(PromptTemplate::update)
    .invoke(request.prompt());
```

The endpoint or admin service that calls this command must reject unauthorized, wrong-tenant, draft/runtime-incompatible, secret-like, or oversized template updates before mutation and must emit trace/audit evidence.

### 3. View over current prompt-template state

```java
@Component(id = "domain-prompt-template-history")
public class DomainPromptTemplateProjection extends View {

  @Consume.FromEventSourcedEntity(PromptTemplate.class)
  public static class PromptTemplateHistoryUpdater extends TableUpdater<PromptTemplateHistoryRow> {
    public Effect<PromptTemplateHistoryRow> onEvent(PromptTemplate.Event event) {
      // Target-project pattern: keep current prompt metadata and update count per template id.
    }
  }
}
```

Expose prompt-template views only through protected, redacted read capabilities. Do not return hidden policy text or provider secrets to browsers or models.

### 4. View over `SessionMemoryEntity`

```java
@Component(id = "session-memory-by-component")
public class SessionMemoryByComponentView extends View {

  @Consume.FromEventSourcedEntity(SessionMemoryEntity.class)
  public static class SessionMemoryUpdater extends TableUpdater<SessionRow> {
    public Effect<SessionRow> onEvent(SessionMemoryEntity.Event event) {
      // Target-project pattern: track last component, message count, and history size.
    }
  }
}
```

Session ids must be scoped to tenant/customer/AuthContext/agent/workflow context. Reject cross-tenant reuse and redact stored content before exposing analytics.

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
  // Target-project pattern: authenticate, authorize, and scope componentId before stream open.
  var source = componentClient
      .forView()
      .stream(SessionMemoryAlertView::continuousByComponent)
      .entriesSource(new SessionMemoryAlertView.FindByComponent(componentId),
          requestContext().lastSeenSseEventId().map(Instant::parse));
  return HttpResponses.serverSentEventsForView(source);
}
```

Authorize before opening streams and filter by tenant/customer context before any event is emitted.

### 6. Threshold alert consumer over session memory

```java
@Component(id = "session-memory-alerts-consumer")
@Consume.FromEventSourcedEntity(SessionMemoryEntity.class)
@Produce.ToTopic("session-memory-alerts")
public class SessionMemoryAlertsConsumer extends Consumer {}
```

Consumer output must preserve event provenance, correlation ids, redaction, and retry/idempotency semantics.

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

Run compaction agents through configured model/provider boundaries, fail closed when provider or security configuration is missing, and trace summary provenance. A model-less compaction placeholder is test-only evidence, not runtime completion.

### 8. Topic audit after compaction

```java
@Component(id = "session-memory-compaction-audit-consumer")
@Consume.FromEventSourcedEntity(SessionMemoryEntity.class)
@Produce.ToTopic("session-memory-compactions")
public class SessionMemoryCompactionAuditConsumer extends Consumer {}
```

## Testing reminders

Prefer:

- `TestModelProvider` for compaction-agent tests and end-to-end compaction flows;
- `withEventSourcedEntityIncomingMessages(SessionMemoryEntity.class)` for session-memory views;
- `withTopicOutgoingMessages("session-memory-compactions")` for audit-topic assertions;
- `withTopicIncomingMessages("session-memory-compactions")` only when you intentionally test topic-backed runtime-state views.

Test authorization denial, wrong-tenant session ids, redaction, oversized memory/template handling, provider-unconfigured fail-closed behavior, and trace/audit emission. Current curated examples do not include built-in `PromptTemplate` or `SessionMemoryEntity` tests; add target-project tests when adopting these APIs.
