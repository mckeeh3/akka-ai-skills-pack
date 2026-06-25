---
name: akka-timed-action-component
description: Implement Akka Java SDK TimedAction components that turn scheduled timer invocations into safe downstream component calls. Use when writing the timed action class itself.
---

# Akka TimedAction Component

Use this skill when the main task is the `TimedAction` class.

## Compile contract gate

Use this skill only for a compile-ready slice under `../docs/app-description-to-code-compile-contract.md`, except for explicitly scoped doc/example maintenance. Before changing generated runtime code, confirm the accepted graph names the responsible worker/harness/actor adapter from `../docs/app-worker-tool-model.md`, the governed-tool and capability contract from `../docs/capability-first-backend-architecture.md`, and this Akka component's role as implementation evidence. If AuthContext, tenant/customer scope, validation, idempotency, denial, audit/trace, side-effect, exposure, or test obligations are missing, repair the brief or block instead of guessing.

## Read first

- `akka-context/sdk/timed-actions.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`

## Component rules

1. Extend `akka.javasdk.timedaction.TimedAction`.
2. Add `@Component(id = "...")`.
3. Keep the class stateless.
4. Treat each timed action method as a scheduled capability execution surface; know the capability id, tenant/customer scope, system principal, approval/policy reference, idempotency key, and audit/trace requirement before writing the handler.
5. Inject only supported dependencies such as `ComponentClient`, `TimerScheduler`, `HttpClientProvider`, `Materializer`, config, or service-setup dependencies.
6. Timed action methods return `TimedAction.Effect`.
7. Use `effects().done()` when the timer work is successfully completed or should be treated as obsolete, denied, stale, or already applied.
8. Use `effects().error(...)` only when you intentionally want the timer invocation to fail.
9. Allow unexpected downstream failures to surface when the retry behavior is desirable.
10. Route consequential work to a workflow/entity command that rechecks authority/scope and records audit/work-trace events instead of committing hidden side effects in the timed action.

## Canonical pattern

Use this shape:

```java
@Component(id = "my-timed-action")
public class MyTimedAction extends TimedAction {

  private final ComponentClient componentClient;

  public MyTimedAction(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  public Effect handle(String id) {
    var result = componentClient
        .forKeyValueEntity(id)
        .method(MyEntity::expire)
        .invoke();

    return switch (result.outcome()) {
      case OK -> effects().done();
      case NOT_FOUND -> effects().done();
      case INVALID -> effects().done();
    };
  }
}
```

## Design notes

- Prefer one method per timer purpose/capability.
- Keep payloads small; timer payloads are limited to 1024 bytes. Include only stable ids and references, then reload authority/context from the authoritative component when needed.
- Make duplicate timer delivery idempotent by using target commands with stable dedupe keys or no-op semantics.
- If a timed action receives a stale, forbidden, or cross-tenant payload, return terminal `done()` after recording the required denial/no-op audit rather than retrying forever.
- If the timed action needs to schedule another timer while handling one, use `timers()` inside the handler.
- The self-rescheduling reference pattern in this repository is `AttentionRefreshTimedAction#sendReminder`.
- If a method used by timers must be renamed later, keep a legacy delegating method for compatibility.

## Generated SaaS compile review

For generated SaaS runtime work, apply the canonical compile contract, worker/tool model, and capability-first backend docs rather than duplicating shared validation, scope, idempotency, audit, and exposure rules here. In this component-specific review, verify the Akka mechanics above preserve the accepted governed-tool context, caller/scope fields, idempotent or no-op behavior, denial/retry semantics, and required tests/traces for the selected exposure path.

## Pair with

- `akka-timers-scheduling` for where to register or delete timers
- `akka-timed-action-testing` for unit or integration coverage
