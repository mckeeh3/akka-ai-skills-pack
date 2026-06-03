# Service-to-service consumers

Small, focused reference for Akka Java SDK service-to-service eventing with Consumer subscribers.

Primary official semantics:
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/sdk/access-control.html.md`
- `akka-context/sdk/running-locally.html.md`

Local producer-side executable example:
- `src/main/java/com/example/application/ShoppingCartPublicEventsConsumer.java`

## Related references

- `docs/service-to-service-views.md` — use when the subscriber should build a query model instead of triggering side effects
- `skills/akka-consumer-from-service-stream/SKILL.md` — focused routing for service-stream consumer subscribers
- `skills/akka-view-from-service-stream/SKILL.md` — focused routing for service-stream view subscribers

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
- add `@Acl` so the subscriber service is allowed to connect
- filter internal-only events with `effects().ignore()`

Example:

```java
@Component(id = "shopping-cart-public-events-consumer")
@Consume.FromEventSourcedEntity(ShoppingCartEntity.class)
@Produce.ServiceStream(id = "shopping_cart_public_events")
@Acl(allow = @Acl.Matcher(service = "*"))
public class ShoppingCartPublicEventsConsumer extends Consumer {

  public sealed interface PublicEvent {
    record ItemAdded(String productId, int quantity) implements PublicEvent {}
    record CheckedOut() implements PublicEvent {}
  }

  public Effect onEvent(ShoppingCart.Event event) {
    return switch (event) {
      case ShoppingCart.Event.ItemAdded added ->
          effects().produce(new PublicEvent.ItemAdded(added.item().productId(), added.item().quantity()));
      case ShoppingCart.Event.CheckedOut ignored -> effects().produce(new PublicEvent.CheckedOut());
      case ShoppingCart.Event.ItemRemoved ignored -> effects().ignore();
      case ShoppingCart.Event.Deleted ignored -> effects().ignore();
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
public sealed interface ShoppingCartPublicEvent {
  record ItemAdded(String productId, int quantity) implements ShoppingCartPublicEvent {}
  record CheckedOut() implements ShoppingCartPublicEvent {}
}

@Component(id = "shopping-cart-public-events-subscriber")
@Consume.FromServiceStream(
    service = "shopping-cart-service",
    id = "shopping_cart_public_events")
public class ShoppingCartPublicEventsSubscriberConsumer extends Consumer {

  public Effect onEvent(ShoppingCartPublicEvent.ItemAdded event) {
    // write to another component, send a notification, or publish to another topic
    return effects().done();
  }

  public Effect onEvent(ShoppingCartPublicEvent.CheckedOut event) {
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
public Effect onEvent(ShoppingCart.Event event) {
  var cartId = messageContext().eventSubject().orElseThrow();
  Metadata metadata = Metadata.EMPTY.add("ce-subject", cartId);
  return effects().produce(new PublicEvent.CheckedOut(), metadata);
}
```

Example subscriber snippet:

```java
public Effect onEvent(ShoppingCartPublicEvent.CheckedOut event) {
  var cartId = messageContext().eventSubject().orElseThrow();
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
- producer uses `@Acl` for the intended subscriber services
- subscriber uses `@Consume.FromServiceStream(service = "...", id = "...")`
- subscriber handles only the public contract
- downstream subscriber behavior is idempotent
- subject metadata is included only when needed
