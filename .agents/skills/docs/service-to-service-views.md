# Service-to-service views

Small, focused reference for Akka Java SDK Views that subscribe to public streams from another Akka service.

Primary official semantics:
- `akka-context/sdk/views.html.md`
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/reference/views/concepts/table-updaters.html.md`
- `akka-context/sdk/running-locally.html.md`

Local related examples:
- `../examples/akka-components/src/main/java/ai/first/application/foundation/workstream/WorkstreamEventAttentionConsumer.java`
- `../examples/akka-components/src/main/java/ai/first/application/foundation/agent/AgentRuntimeTraceView.java`

## Related references

- `./service-to-service-consumers.md` — use when the subscriber should trigger side effects instead of building a query model
- `../akka-view-from-service-stream/SKILL.md` — focused routing for service-stream view subscribers
- `../akka-consumer-from-service-stream/SKILL.md` — focused routing for service-stream consumer subscribers

## When to use this pattern

Use a service-to-service View when:
- one Akka service publishes a public stream with `@Produce.ServiceStream`
- another Akka service needs a query model built from that stream
- the subscriber should expose searchable state rather than trigger side effects

If the subscriber needs side effects, downstream writes, or notifications instead of a query model, use a Consumer subscriber instead.

## Publisher-side pattern

A publisher is usually a Consumer that reads local entity events or state changes and republishes a public contract.

Rules:
- publish a public contract, not internal entity events
- use `@Produce.ServiceStream(id = "...")`
- add `@Acl` so subscriber services can connect
- filter internal-only events with `effects().ignore()`

Example producer:

```java
@Component(id = "workstream-event-public-events-consumer")
@Consume.FromEventSourcedEntity(AgentDefinitionEntity.class)
@Produce.ServiceStream(id = "workstream_event_public_events")
@Acl(allow = @Acl.Matcher(service = "*"))
public class WorkstreamEventAttentionConsumer extends Consumer {

  public sealed interface PublicEvent {
    record ItemAdded(String productId, int quantity) implements PublicEvent {}
    record CheckedOut() implements PublicEvent {}
  }

  public Effect onEvent(WorkstreamEvent.Event event) {
    return switch (event) {
      case WorkstreamEvent.Event.ItemAdded added ->
          effects().produce(new PublicEvent.ItemAdded(added.item().productId(), added.item().quantity()));
      case WorkstreamEvent.Event.CheckedOut ignored -> effects().produce(new PublicEvent.CheckedOut());
      default -> effects().ignore();
    };
  }
}
```

## Subscriber-side view pattern

The subscriber View listens to the producer's public stream and updates rows in a `TableUpdater`.

Rules:
- subscribe with `@Consume.FromServiceStream(service = "...", id = "...")`
- consume only public event types
- use `updateContext().eventSubject()` when the publisher includes subject metadata
- use `effects().updateRow(...)`, `deleteRow()`, or `ignore()` explicitly
- remember eventual consistency for queries

Minimal subscriber example:

```java
@Component(id = "workstream-event-public-view")
public class WorkstreamEventPublicView extends View {

  public sealed interface WorkstreamEventPublicEvent {
    record ItemAdded(String productId, int quantity) implements WorkstreamEventPublicEvent {}
    record CheckedOut() implements WorkstreamEventPublicEvent {}
  }

  public record WorkstreamSummary(String workstreamId, int itemCount, String status) {}
  public record WorkstreamSummaries(List<WorkstreamSummary> entries) {}
  public record FindByStatus(String status, String minWorkstreamId) {}

  @Consume.FromServiceStream(
      service = "workstream-event-service",
      id = "workstream_event_public_events")
  public static class WorkstreamUpdater extends TableUpdater<WorkstreamSummary> {

    public Effect<WorkstreamSummary> onEvent(WorkstreamEventPublicEvent.ItemAdded event) {
      var workstreamId = updateContext().eventSubject().orElse("");
      var current = rowState() == null ? new WorkstreamSummary(workstreamId, 0, "ACTIVE") : rowState();
      return effects().updateRow(new WorkstreamSummary(workstreamId, current.itemCount() + event.quantity(), "ACTIVE"));
    }

    public Effect<WorkstreamSummary> onEvent(WorkstreamEventPublicEvent.CheckedOut event) {
      var current = rowState();
      return effects().updateRow(new WorkstreamSummary(current.workstreamId(), current.itemCount(), "CHECKED_OUT"));
    }
  }

  @Query(
      """
      SELECT * AS entries
      FROM workstream_event_public_view
      WHERE status = :status
        AND workstreamId >= :minWorkstreamId
      ORDER BY workstreamId
      """)
  public QueryEffect<WorkstreamSummaries> getByStatus(FindByStatus request) {
    return queryResult();
  }
}
```

## Handler-shape rules

For service-stream views:
- use `onEvent(...)` for event-style public stream messages
- keep one handler per public message type when that is clearer
- use `rowState()` for incremental projections
- use a wrapper record such as `WorkstreamSummaries(List<WorkstreamSummary> entries)` for multi-row queries
- include every `ORDER BY` column in the same query's `WHERE` conditions, for example `workstreamId >= :minWorkstreamId` when ordering by `workstreamId`

## Contract rules

Treat these as the stable cross-service boundary:
- producer service name
- stream id
- public event types
- any required metadata such as `ce-subject`
- ACL policy

Do not couple the subscriber view to:
- producer internal entity event classes
- producer internal package layout
- producer-only private event variants

## Subject metadata

If each event belongs to a stable aggregate or business id, include `ce-subject` on the producer side so the subscriber view can select a row key.

Producer snippet:

```java
public Effect onEvent(WorkstreamEvent.Event event) {
  var workstreamId = messageContext().eventSubject().orElseThrow();
  Metadata metadata = Metadata.EMPTY.add("ce-subject", workstreamId);
  return effects().produce(new PublicEvent.CheckedOut(), metadata);
}
```

Subscriber snippet:

```java
public Effect<WorkstreamSummary> onEvent(WorkstreamEventPublicEvent.CheckedOut event) {
  var workstreamId = updateContext().eventSubject().orElseThrow();
  var current = rowState() == null ? new WorkstreamSummary(workstreamId, 0, "ACTIVE") : rowState();
  return effects().updateRow(new WorkstreamSummary(workstreamId, current.itemCount(), "CHECKED_OUT"));
}
```

## Query design reminders

Prefer:
- a transformed row model tailored to subscriber queries
- wrapper response records for multi-row queries
- explicit aliases such as `SELECT * AS entries`
- stable `@Component(id = "...")` values

Remember:
- service-stream views are eventually consistent
- cross-service streams are for propagation; query streaming is for clients and does not replace event propagation
- incompatible view changes may require a new `@Component(id = "...")`

## Local testing guidance

This repository does not include a full two-service executable view subscriber fixture.

For real local testing:
- run producer and subscriber as separate Akka services
- use different local ports
- verify the producer ACL allows the subscriber service
- publish producer events and query the subscriber view until it reflects the public stream

Read:
- `akka-context/sdk/running-locally.html.md`

## Checklist

Before finishing, verify:
- producer uses `@Produce.ServiceStream(id = "...")`
- producer `@Acl` allows the intended subscriber services
- subscriber updater uses `@Consume.FromServiceStream(service = "...", id = "...")`
- subscriber consumes only public event types
- row key selection is explicit, typically via `updateContext().eventSubject()`
- query methods use wrapper aliases correctly
- view behavior assumes eventual consistency
