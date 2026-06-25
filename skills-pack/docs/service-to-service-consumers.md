# Service-to-service consumers

Small, focused reference for Akka Java SDK service-to-service eventing with Consumer subscribers.

Primary official semantics:
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/access-control.html.md`
- `akka-context/sdk/running-locally.html.md`

Local governed projection example (not a full two-service executable fixture):
- `../examples/akka-components/src/main/java/ai/first/application/foundation/workstream/WorkstreamEventAttentionConsumer.java`

## Related references

- `./service-to-service-views.md` — use when the subscriber should build a query model instead of triggering side effects
- `../akka-consumer-from-service-stream/SKILL.md` — focused routing for service-stream consumer subscribers
- `../akka-view-from-service-stream/SKILL.md` — focused routing for service-stream view subscribers

## When to use this pattern

Use a service-to-service Consumer when:
- one Akka service publishes a public stream with `@Produce.ServiceStream`
- another Akka service needs side effects or downstream writes from that stream
- the subscriber is not just building a query model

If the subscriber only needs a query model, prefer a View subscriber instead.

## Publisher-side pattern

A publisher is a Consumer that consumes local events or state changes and republishes a public contract.

Rules:
- consume local internal events or state
- map them into a public event contract
- publish with `@Produce.ServiceStream(id = "...")`
- add `@Acl` for the intended subscriber service names; avoid wildcard service ACLs except in explicitly bounded local fixtures
- filter internal-only events with `effects().ignore()`
- include tenant/customer scope, provenance, correlation, and subject metadata when downstream authorization or idempotency depends on it

Example:

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
      case OrderEvent.ItemRemoved ignored -> effects().ignore();
      case OrderEvent.Deleted ignored -> effects().ignore();
    };
  }
}
```

## Subscriber-side consumer pattern

A subscriber Consumer reads the public stream from another service.

Rules:
- subscribe with `@Consume.FromServiceStream(service = "...", id = "...")`
- consume the public event contract, not the publisher's internal entity events
- make downstream writes idempotent because delivery is at least once
- use `messageContext().eventSubject()` if the publisher included subject metadata

Minimal subscriber example:

```java
public sealed interface OrderPublicEvent {
  record ItemAdded(String productId, int quantity) implements OrderPublicEvent {}
  record CheckedOut() implements OrderPublicEvent {}
}

@Component(id = "order-public-events-subscriber")
@Consume.FromServiceStream(
    service = "order-service",
    id = "order_public_events")
public class OrderPublicEventsSubscriberConsumer extends Consumer {

  public Effect onEvent(OrderPublicEvent.ItemAdded event) {
    // Use a governed downstream capability path for writes, notifications, or republishing.
    return effects().done();
  }

  public Effect onEvent(OrderPublicEvent.CheckedOut event) {
    return effects().done();
  }
}
```

## Contract rules

Treat these as part of the cross-service API:
- producer service name
- stream id
- public message types
- ACL policy allowing subscriber services
- metadata needed for scope, provenance, correlation, idempotency, and audit/work traces

Avoid coupling subscribers to:
- producer internal entity event types
- producer internal package layout
- producer-only event variants that should remain private

## Sharing the public contract

Choose one approach intentionally:

### Option 1: shared public API module
Use when multiple services need the same event contract.

Pros:
- one source of truth
- less duplication

Tradeoff:
- shared versioning discipline is required

### Option 2: duplicated public DTOs in the subscriber
Use when the contract is small and you want looser coupling.

Pros:
- subscriber compiles independently
- explicit public boundary

Tradeoff:
- contract changes must be synchronized carefully

## Subject metadata

If downstream routing or ordering depends on a stable subject, include it on the producer side.

Example producer snippet:

```java
public Effect onEvent(OrderEvent event) {
  var orderId = messageContext().eventSubject().orElseThrow();
  Metadata metadata = Metadata.EMPTY.add("ce-subject", orderId);
  return effects().produce(new PublicEvent.CheckedOut(), metadata);
}
```

Example subscriber snippet:

```java
public Effect onEvent(OrderPublicEvent.CheckedOut event) {
  var orderId = messageContext().eventSubject().orElseThrow();
  return effects().done();
}
```

## Testing guidance

This repository does not include a full two-service executable subscriber fixture.

For local testing of a real subscriber service:
- run producer and subscriber as separate Akka services
- ensure they use different local ports
- verify the producer `@Acl` allows the subscriber service
- publish a producer event and assert the subscriber side effect

Read:
- `akka-context/sdk/running-locally.html.md`

## Checklist

Before finishing, verify:
- producer uses `@Produce.ServiceStream(id = "...")`
- producer uses specific `@Acl` entries for the intended subscriber services
- subscriber uses `@Consume.FromServiceStream(service = "...", id = "...")`
- subscriber handles only the public contract
- downstream subscriber behavior is idempotent and goes through governed capability paths for protected side effects
- subject metadata is included when routing, scope, idempotency, or audit depends on it
- real local producer/subscriber smoke evidence is recorded before claiming runtime-ready service-to-service behavior
