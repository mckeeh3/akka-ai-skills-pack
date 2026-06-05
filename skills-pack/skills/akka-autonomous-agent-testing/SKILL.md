---
name: akka-autonomous-agent-testing
description: Test Akka Autonomous Agents with TestKitSupport, TestModelProvider.AutonomousAgentTools, Awaitility, typed task snapshots, results, notifications, and coordination scripts.
---

# Akka Autonomous Agent Testing

Use this skill when validating Autonomous Agent components, task lifecycles, task rules, tool calls, delegation, handoff, teams, moderation, notifications, or generated-app governance around autonomous tasks.

## Required reading

Read before validating generated-app worker tasks:
- `../docs/autonomous-agent-worker-runtime-pattern.md`

Read when API details are needed:
- `../../../specs/autonomous-agents-integration/research-notes.md`
- `akka-context/sdk/autonomous-agents/testing.html.md`
- `akka-context/sdk/autonomous-agents/client.html.md`
- `akka-context/sdk/autonomous-agents/notifications.html.md`

## Core pattern

1. Extend `TestKitSupport`.
2. Create one `TestModelProvider` per autonomous agent class when behavior differs.
3. Register providers in `testKitSettings()` with `.withModelProvider(AgentClass.class, provider)`.
4. Trigger through `ComponentClient` or endpoint paths; do not call fake services as the normal runtime path.
5. Use `TestModelProvider.AutonomousAgentTools` for built-in autonomous tool calls:
   - `completeTask(result)` / `completeTaskJson(json)`
   - `failTask(reason)`
   - `handoffTo(Target.class, context)`
   - `delegateTo(taskDefinition, Worker.class, instructions)`
   - request-based delegation helpers
   - team, messaging, and moderation helpers
6. Poll with Awaitility until `componentClient.forTask(taskId).get(TASK)` reaches a result or terminal status.
7. Assert typed result fields, status, failure reason, task rule rejection, traces, and notification exposure when relevant.
8. Reset reused model providers in `@AfterEach`.

## Script examples

Single task completion:

```java
model.whenMessage(msg -> true)
  .thenInvoke(AutonomousAgentTools.completeTask(new ReviewResult("ok")));
```

Rejected then accepted rule:

```java
model.whenMessage(msg -> true)
  .thenInvoke(AutonomousAgentTools.completeTask(new ResultWithoutSources()))
  .thenInvoke(AutonomousAgentTools.completeTask(new ResultWithSources(List.of("doc-1"))));
```

Coordination:
- register separate model providers for coordinator and workers;
- script the coordinator to `delegateTo(...)` or `handoffTo(...)`;
- script workers to complete their assigned tasks;
- assert top-level task result and worker task snapshots.

## Generated SaaS tests

For generated-app autonomous work, and especially worker tasks covered by `../docs/autonomous-agent-worker-runtime-pattern.md`, add tests for:
- task start/query/result authorization and tenant isolation;
- disabled user or disabled managed-agent denial;
- model policy/provider-secret non-exposure and fail-closed missing config;
- `ToolPermissionBoundary` denial for ungranted local/component/MCP/readSkill/readReferenceDoc tools;
- approval-required side effects and no side effect before approval;
- task id/agent instance id/customer scope validation;
- task creation idempotency where exposed through retriable APIs;
- trace emission for task creation, assignment, completion/failure, tool calls, delegation, handoff, notification exposure, and denials;
- typed `worker.task.*` event payload/source refs, attention upsert/resolve behavior, and structured progress/result surface/API contracts;
- provider fail-closed and no fake success scans for deterministic/demo/model-less runtime substitutes;
- endpoint/workflow/UI smoke path when the task represents a named runtime feature.

## Runtime completion rule

`TestModelProvider` is test-only. It can prove contracts and denials, but a generated-app feature is complete only when the intended local Akka runtime/API/UI path uses the real `AutonomousAgent` component and fails closed for missing provider/security configuration. Do not mark a feature done with deterministic/model-less normal runtime substitutes.

## Review checklist

- each AutonomousAgent class has a registered test provider;
- assertions use task snapshots/results, not only notifications;
- asynchronous completion uses Awaitility or equivalent polling;
- task rules are tested for rejection and retry/failure behavior;
- coordination tests cover both coordinator and worker paths;
- generated-app tests include authorization, scope, tool-boundary, trace, and fail-closed checks.
