# Consumer reference

Small, agent-oriented reference for Akka Java SDK Consumers.

Primary official semantics:
- `akka-context/sdk/consuming-producing.html.md`

Curated projection pattern example:
- `../examples/akka-components/src/main/java/ai/first/application/foundation/workstream/WorkstreamEventAttentionConsumer.java`

## Quick source selection

Use this source when:

- `@Consume.FromEventSourcedEntity` → downstream logic depends on persisted facts/events
- `@Consume.FromKeyValueEntity` → downstream logic depends on latest state snapshots
- `@Consume.FromWorkflow` → downstream logic depends on workflow status/state updates
- `@Consume.FromTopic` → external broker messages should drive this service
- `@Consume.FromServiceStream` → another Akka service publishes a public stream for this service

All consumer flows are at-least-once.

Design for:
- duplicate delivery
- idempotent downstream writes
- explicit `effects().ignore()` for unhandled cases
- preserved tenant/customer scope, source refs, correlation/idempotency keys, and audit/work trace provenance for protected downstream effects
- backend capability authorization at the producer/caller or consumer boundary; never rely on topic names, frontend visibility, or payload fields alone as authority

## Minimal patterns

### Event sourced entity consumer

```java
@Component(id = "workstream-event-attention-consumer")
@Consume.FromEventSourcedEntity(AgentDefinitionEntity.class)
public class WorkstreamEventAttentionConsumer extends Consumer {

  public Effect onEvent(WorkstreamEvent.Event event) {
    return switch (event) {
      case WorkstreamEvent.Event.CheckedOut checkedOut -> effects().done();
      default -> effects().ignore();
    };
  }
}
```

### Key value entity consumer

```java
@Component(id = "workstream-log-attention-consumer")
@Consume.FromKeyValueEntity(DurableIdentityRepositoryEntity.class)
public class WorkstreamEventAttentionConsumer extends Consumer {

  public Effect onChange(WorkstreamLog.State state) {
    if (!state.checkedOut()) {
      return effects().ignore();
    }
    return effects().done();
  }

  @DeleteHandler
  public Effect onDelete() {
    return effects().ignore();
  }
}
```

### Topic consumer

```java
@Component(id = "workstream-event-commands-topic-consumer")
@Consume.FromTopic("workstream-event-commands")
public class WorkstreamEventAttentionConsumer extends Consumer {

  public record Attention() {}

  public Effect onMessage(Attention ignored) {
    var workstreamId = messageContext().eventSubject().orElseThrow();
    return effects().done();
  }
}
```

## Service-to-service eventing pattern

Use this when one Akka service publishes a public event stream and another Akka service subscribes to it.

Dedicated reference:
- `./service-to-service-consumers.md`

Local projection-side pattern example:
- `../examples/akka-components/src/main/java/ai/first/application/foundation/workstream/WorkstreamEventAttentionConsumer.java`

## Topic production with subject metadata

Use `ce-subject` when downstream routing or per-entity ordering matters.

```java
@Component(id = "workstream-event-events-to-topic-consumer")
@Consume.FromEventSourcedEntity(AgentDefinitionEntity.class)
@Produce.ToTopic("workstream-event-events")
public class WorkstreamEventAttentionConsumer extends Consumer {

  public Effect onEvent(WorkstreamEvent.Event event) {
    var workstreamId = messageContext().eventSubject().orElseThrow();
    Metadata metadata = Metadata.EMPTY.add("ce-subject", workstreamId);
    return effects().produce(event, metadata);
  }
}
```

## Testing reminders

Prefer:
- `TestKitSupport`
- `withTopicIncomingMessages(...)` for topic-ingesting consumers
- `withTopicOutgoingMessages(...)` for topic-producing consumers
- `withWorkflowIncomingMessages(...)` for workflow-driven consumers
- end-to-end tests for same-service entity consumers

Reference test:
- `../examples/akka-components/src/test/java/ai/first/application/foundation/workstream/WorkstreamEventBackboneServiceTest.java`

For generated-app workstream consumers, tests should also cover duplicate delivery, scope mismatch or unsupported-event denial, safe no-op behavior, and audit/work trace evidence for consequential projections or side effects.
