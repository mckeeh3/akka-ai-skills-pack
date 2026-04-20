# Consumer reference

Small, agent-oriented reference for Akka Java SDK Consumers.

Primary official semantics:
- `akka-context/sdk/consuming-producing.html.md`

Local executable examples:
- `src/main/java/com/example/application/ShoppingCartCheckoutConsumer.java`
- `src/main/java/com/example/application/DraftCartCheckoutConsumer.java`
- `src/main/java/com/example/application/ShoppingCartCommandsTopicConsumer.java`
- `src/main/java/com/example/application/ShoppingCartEventsToTopicConsumer.java`
- `src/main/java/com/example/application/ShoppingCartPublicEventsConsumer.java`
- `src/main/java/com/example/application/ReviewWorkflowTopicConsumer.java`

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

## Minimal patterns

### Event sourced entity consumer

```java
@Component(id = "shopping-cart-checkout-consumer")
@Consume.FromEventSourcedEntity(ShoppingCartEntity.class)
public class ShoppingCartCheckoutConsumer extends Consumer {

  public Effect onEvent(ShoppingCart.Event event) {
    return switch (event) {
      case ShoppingCart.Event.CheckedOut checkedOut -> effects().done();
      default -> effects().ignore();
    };
  }
}
```

### Key value entity consumer

```java
@Component(id = "draft-cart-checkout-consumer")
@Consume.FromKeyValueEntity(DraftCartEntity.class)
public class DraftCartCheckoutConsumer extends Consumer {

  public Effect onChange(DraftCart.State state) {
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
@Component(id = "shopping-cart-commands-topic-consumer")
@Consume.FromTopic("shopping-cart-commands")
public class ShoppingCartCommandsTopicConsumer extends Consumer {

  public record Checkout() {}

  public Effect onMessage(Checkout ignored) {
    var cartId = messageContext().eventSubject().orElseThrow();
    return effects().done();
  }
}
```

## Service-to-service eventing pattern

Use this when one Akka service publishes a public event stream and another Akka service subscribes to it.

Dedicated reference:
- `docs/service-to-service-consumers.md`

Local producer-side executable example:
- `src/main/java/com/example/application/ShoppingCartPublicEventsConsumer.java`

## Topic production with subject metadata

Use `ce-subject` when downstream routing or per-entity ordering matters.

```java
@Component(id = "shopping-cart-events-to-topic-consumer")
@Consume.FromEventSourcedEntity(ShoppingCartEntity.class)
@Produce.ToTopic("shopping-cart-events")
public class ShoppingCartEventsToTopicConsumer extends Consumer {

  public Effect onEvent(ShoppingCart.Event event) {
    var cartId = messageContext().eventSubject().orElseThrow();
    Metadata metadata = Metadata.EMPTY.add("ce-subject", cartId);
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

Reference tests:
- `src/test/java/com/example/application/ShoppingCartCheckoutConsumerIntegrationTest.java`
- `src/test/java/com/example/application/DraftCartCheckoutConsumerIntegrationTest.java`
- `src/test/java/com/example/application/ShoppingCartCommandsTopicConsumerIntegrationTest.java`
- `src/test/java/com/example/application/ReviewWorkflowTopicConsumerIntegrationTest.java`
