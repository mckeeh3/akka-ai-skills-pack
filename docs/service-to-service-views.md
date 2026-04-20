# Service-to-service views

Small, focused reference for Akka Java SDK Views that subscribe to public streams from another Akka service.

Primary official semantics:
- `akka-context/sdk/views.html.md`
- `akka-context/sdk/consuming-producing.html.md`
- `akka-context/reference/views/concepts/table-updaters.html.md`
- `akka-context/sdk/running-locally.html.md`

Local related examples:
- `src/main/java/com/example/application/ShoppingCartPublicEventsConsumer.java`
- `src/main/java/com/example/application/ReviewRequestsByStatusView.java`
- `src/main/java/com/example/application/ShoppingCartTopicView.java`

## Related references

- `docs/service-to-service-consumers.md` — use when the subscriber should trigger side effects instead of building a query model
- `.pi/skills/akka-view-from-service-stream/SKILL.md` — focused routing for service-stream view subscribers
- `.pi/skills/akka-consumer-from-service-stream/SKILL.md` — focused routing for service-stream consumer subscribers

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
@Component(id = "shopping-cart-public-view")
public class ShoppingCartPublicView extends View {

  public sealed interface ShoppingCartPublicEvent {
    record ItemAdded(String productId, int quantity) implements ShoppingCartPublicEvent {}
    record CheckedOut() implements ShoppingCartPublicEvent {}
  }

  public record CartSummary(String cartId, int itemCount, String status) {}
  public record CartSummaries(List<CartSummary> entries) {}
  public record FindByStatus(String status) {}

  @Consume.FromServiceStream(
      service = "shopping-cart-service",
      id = "shopping_cart_public_events")
  public static class CartUpdater extends TableUpdater<CartSummary> {

    public Effect<CartSummary> onEvent(ShoppingCartPublicEvent.ItemAdded event) {
      var cartId = updateContext().eventSubject().orElse("");
      var current = rowState() == null ? new CartSummary(cartId, 0, "ACTIVE") : rowState();
      return effects().updateRow(new CartSummary(cartId, current.itemCount() + event.quantity(), "ACTIVE"));
    }

    public Effect<CartSummary> onEvent(ShoppingCartPublicEvent.CheckedOut event) {
      var current = rowState();
      return effects().updateRow(new CartSummary(current.cartId(), current.itemCount(), "CHECKED_OUT"));
    }
  }

  @Query(
      """
      SELECT * AS entries
      FROM shopping_cart_public_view
      WHERE status = :status
      ORDER BY cartId
      """)
  public QueryEffect<CartSummaries> getByStatus(FindByStatus request) {
    return queryResult();
  }
}
```

## Handler-shape rules

For service-stream views:
- use `onEvent(...)` for event-style public stream messages
- keep one handler per public message type when that is clearer
- use `rowState()` for incremental projections
- use a wrapper record such as `CartSummaries(List<CartSummary> entries)` for multi-row queries

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
public Effect onEvent(ShoppingCart.Event event) {
  var cartId = messageContext().eventSubject().orElseThrow();
  Metadata metadata = Metadata.EMPTY.add("ce-subject", cartId);
  return effects().produce(new PublicEvent.CheckedOut(), metadata);
}
```

Subscriber snippet:

```java
public Effect<CartSummary> onEvent(ShoppingCartPublicEvent.CheckedOut event) {
  var cartId = updateContext().eventSubject().orElseThrow();
  var current = rowState() == null ? new CartSummary(cartId, 0, "ACTIVE") : rowState();
  return effects().updateRow(new CartSummary(cartId, current.itemCount(), "CHECKED_OUT"));
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
