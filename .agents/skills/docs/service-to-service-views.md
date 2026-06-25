# Service-to-service views

Small, focused reference for Akka Java SDK Views that subscribe to public streams from another Akka service.

Primary official semantics:
- `akka-context/sdk/views.html.md`
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/reference/views/concepts/table-updaters.html.md`
- `akka-context/sdk/running-locally.html.md`

Local related examples (not a full two-service executable fixture):
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
- add `@Acl` for the intended subscriber service names; avoid wildcard service ACLs except in explicitly bounded local fixtures
- filter internal-only events with `effects().ignore()`
- include tenant/customer scope, provenance, correlation, and subject metadata when subscriber query authorization or row idempotency depends on it

Example producer:

```java
@Component(id = "order-public-events-consumer")
@Consume.FromEventSourcedEntity(OrderEntity.class)
@Produce.ServiceStream(id = "order_public_events")
@Acl(allow = @Acl.Matcher(service = "order-read-side-service"))
public class OrderPublicEventsConsumer extends Consumer {

  public sealed interface PublicEvent {
    record ItemAdded(String productId, int quantity) implements PublicEvent {}
    record CheckedOut() implements PublicEvent {}
  }

  public Effect onEvent(OrderEvent event) {
    return switch (event) {
      case OrderEvent.ItemAdded added ->
          effects().produce(new PublicEvent.ItemAdded(added.item().productId(), added.item().quantity()));
      case OrderEvent.CheckedOut ignored -> effects().produce(new PublicEvent.CheckedOut());
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
- enforce tenant/customer scope and backend authorization in the API/query surface that exposes the view; the stream subscription itself is not user authorization

Minimal subscriber example:

```java
@Component(id = "order-public-view")
public class OrderPublicView extends View {

  public sealed interface OrderPublicEvent {
    record ItemAdded(String productId, int quantity) implements OrderPublicEvent {}
    record CheckedOut() implements OrderPublicEvent {}
  }

  public record OrderSummary(String orderId, int itemCount, String status) {}
  public record OrderSummaries(List<OrderSummary> entries) {}
  public record FindByStatus(String status, String minOrderId) {}

  @Consume.FromServiceStream(
      service = "order-service",
      id = "order_public_events")
  public static class OrderUpdater extends TableUpdater<OrderSummary> {

    public Effect<OrderSummary> onEvent(OrderPublicEvent.ItemAdded event) {
      var orderId = updateContext().eventSubject().orElse("");
      var current = rowState() == null ? new OrderSummary(orderId, 0, "ACTIVE") : rowState();
      return effects().updateRow(new OrderSummary(orderId, current.itemCount() + event.quantity(), "ACTIVE"));
    }

    public Effect<OrderSummary> onEvent(OrderPublicEvent.CheckedOut event) {
      var current = rowState();
      return effects().updateRow(new OrderSummary(current.orderId(), current.itemCount(), "CHECKED_OUT"));
    }
  }

  @Query(
      """
      SELECT * AS entries
      FROM order_public_view
      WHERE status = :status
        AND orderId >= :minOrderId
      ORDER BY orderId
      """)
  public QueryEffect<OrderSummaries> getByStatus(FindByStatus request) {
    return queryResult();
  }
}
```

## Handler-shape rules

For service-stream views:
- use `onEvent(...)` for event-style public stream messages
- keep one handler per public message type when that is clearer
- use `rowState()` for incremental projections
- use a wrapper record such as `OrderSummaries(List<OrderSummary> entries)` for multi-row queries
- include every `ORDER BY` column in the same query's `WHERE` conditions, for example `orderId >= :minOrderId` when ordering by `orderId`

## Contract rules

Treat these as the stable cross-service boundary:
- producer service name
- stream id
- public event types
- any required metadata such as `ce-subject`, scope, provenance, correlation, and audit/work-trace identifiers
- ACL policy for intended subscriber services

Do not couple the subscriber view to:
- producer internal entity event classes
- producer internal package layout
- producer-only private event variants

## Subject metadata

If each event belongs to a stable aggregate or business id, include `ce-subject` on the producer side so the subscriber view can select a row key.

Producer snippet:

```java
public Effect onEvent(OrderEvent event) {
  var orderId = messageContext().eventSubject().orElseThrow();
  Metadata metadata = Metadata.EMPTY.add("ce-subject", orderId);
  return effects().produce(new PublicEvent.CheckedOut(), metadata);
}
```

Subscriber snippet:

```java
public Effect<OrderSummary> onEvent(OrderPublicEvent.CheckedOut event) {
  var orderId = updateContext().eventSubject().orElseThrow();
  var current = rowState() == null ? new OrderSummary(orderId, 0, "ACTIVE") : rowState();
  return effects().updateRow(new OrderSummary(orderId, current.itemCount(), "CHECKED_OUT"));
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
- producer `@Acl` allows only the intended subscriber services
- subscriber updater uses `@Consume.FromServiceStream(service = "...", id = "...")`
- subscriber consumes only public event types
- row key selection is explicit, typically via `updateContext().eventSubject()`
- query methods use wrapper aliases correctly and exposed query APIs enforce AuthContext plus tenant/customer scope
- view behavior assumes eventual consistency
- real local producer/subscriber smoke evidence is recorded before claiming runtime-ready service-to-service behavior
