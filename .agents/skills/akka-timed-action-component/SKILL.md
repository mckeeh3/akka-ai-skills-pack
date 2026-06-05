---
name: akka-timed-action-component
description: Implement Akka Java SDK TimedAction components that turn scheduled timer invocations into safe downstream component calls. Use when writing the timed action class itself.
---

# Akka TimedAction Component

Use this skill when the main task is the `TimedAction` class.

## Generated SaaS input contract

For generated full-stack AI-first SaaS work, implement only after the selected task, app-description, spec, or backlog supplies or explicitly defers:
- functional agent or explicit internal-only/foundation scope;
- workstream, structured surface id/type/version, and surface action or workstream event when user-facing;
- capability id/class, selected Akka substrate, and exposure surfaces;
- `AuthContext`, tenant/customer scope, roles/capabilities, and backend authorization boundary;
- input/output DTOs, redaction, side effects, idempotency, policy/approval/escalation, audit/work traces, and required tests.

If these are absent and the work is generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or block for task-brief repair instead of guessing.

## Read first

- `akka-context/sdk/timed-actions.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../examples/akka-components/src/main/java/com/example/application/TicketReservationTimedAction.java`
- `../examples/akka-components/src/main/java/com/example/application/TicketReservationEntity.java`
- `../examples/akka-components/src/test/java/com/example/application/TicketReservationTimedActionTest.java`
- `../examples/akka-components/src/test/java/com/example/application/TicketReservationEndpointIntegrationTest.java`
- `../examples/akka-components/src/main/java/com/example/application/ReminderJobTimedAction.java`
- `../examples/akka-components/src/main/java/com/example/application/ReminderJobEntity.java`
- `../examples/akka-components/src/test/java/com/example/application/ReminderJobTimedActionTest.java`
- `../examples/akka-components/src/test/java/com/example/application/ReminderJobEndpointIntegrationTest.java`

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
- The self-rescheduling reference pattern in this repository is `ReminderJobTimedAction#sendReminder`.
- If a method used by timers must be renamed later, keep a legacy delegating method for compatibility.

## Generated SaaS timer contract

For generated SaaS scheduled capabilities, require:
- scheduled capability id, scheduler authority basis, tenant/customer target scope, and system principal;
- small timer payloads containing stable ids/references only;
- idempotency/no-op strategy for duplicate, stale, obsolete, denied, or cross-tenant invocations;
- policy/approval reference, retry budget, and escalation behavior for consequential work;
- audit/work-trace records for scheduling, execution, denial/no-op, retry exhaustion, and side effects;
- tests for authorized execution, stale/no-op, forbidden/cross-tenant, retry/idempotency, audit/trace, and surface/realtime updates where exposed.


## Pair with

- `akka-timers-scheduling` for where to register or delete timers
- `akka-timed-action-testing` for unit or integration coverage
