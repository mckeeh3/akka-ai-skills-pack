---
name: akka-autonomous-agent-tasks
description: Define and use Akka Autonomous Agent Task, TaskTemplate, TaskAcceptance, TaskRule, dependencies, attachments, and task lifecycle operations.
---

# Akka Autonomous Agent Tasks

Use this skill when the work is mainly about task definitions, task instances, rules, dependencies, attachments, task client calls, or lifecycle state.

## Required reading

Read before defining generated-app worker task contracts:
- `../docs/autonomous-agent-worker-runtime-pattern.md`

Read when API details are needed:
- `../docs/autonomous-agents-api-notes.md`
- `akka-context/sdk/autonomous-agents/tasks.html.md`
- `akka-context/sdk/autonomous-agents/client.html.md`
- `akka-context/sdk/autonomous-agents/notifications.html.md`

## Core concepts

- `Task<R>` defines task name, description, result type, instructions, rules, attachments, and dependencies.
- If `resultConformsTo(...)` is omitted, the result defaults to `String`.
- `TaskTemplate<R>` parameterizes reusable task instructions with placeholders; `params(...)` resolves placeholders, while `instructions(...)` replaces the template instructions.
- `TaskAcceptance.of(TASK...)` declares accepted task types and can set `maxIterationsPerTask(n)` or `canHandoffTo(...)`.
- `TaskRule<R>` validates structured results; rejected results move to `RESULT_REJECTED` and the model gets the rejection reason on the next iteration.
- Dependencies delay a task until all dependencies complete; failed or cancelled dependencies cancel dependents.
- Attachments can include text, images, PDFs, URI-backed content, or custom content loaded by a definition content loader.

## Task lifecycle

Statuses:
- `PENDING`
- `ASSIGNED`
- `IN_PROGRESS`
- `RESULT_REJECTED`
- `COMPLETED`
- `FAILED`
- `CANCELLED`

Terminal states are `COMPLETED`, `FAILED`, and `CANCELLED`. Failed tasks do not retry automatically; create a new task when retry is desired.

## Client patterns

Auto-stopping single task:

```java
String taskId = componentClient
  .forAutonomousAgent(MyAgent.class, agentInstanceId)
  .runSingleTask(MyTasks.REVIEW.instructions("Review this case"));
```

Managed task lifecycle:

```java
String taskId = componentClient
  .forTask(taskIdSeed)
  .create(MyTasks.REVIEW.instructions("Review this case"));

componentClient
  .forAutonomousAgent(MyAgent.class, agentInstanceId)
  .assignTasks(taskId);
```

Task operations:
- `forTask(taskId).create(task)`
- `forTask(taskId).get(taskDefinition)`
- `forTask(taskId).result(taskDefinition)`
- `forTask(taskId).assign(owner)`
- `forTask(taskId).complete(taskDefinition, result)`
- `forTask(taskId).fail(reason)`
- `forTask(taskId).notificationStream()`

## Generated SaaS guardrails

For worker-style generated-app tasks, apply `../docs/autonomous-agent-worker-runtime-pattern.md` before coding the task contract. Use the curated User Admin examples for concrete task shape and runtime boundaries: `../examples/akka-components/src/main/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewTasks.java`, `../examples/akka-components/src/main/java/ai/first/application/coreapp/useradmin/AccessReviewAutonomousAgentResultRule.java`, `../examples/akka-components/src/main/java/ai/first/application/coreapp/useradmin/ComponentClientAccessReviewAutonomousAgentRuntime.java`, and `../examples/akka-components/src/main/java/ai/first/application/coreapp/useradmin/FailClosedAccessReviewAutonomousAgentRuntime.java`. Every task operation exposed through HTTP, UI, workflow, tool, timer, consumer, or MCP is a governed capability. Preserve:
- tenant/customer-scoped task ids and agent instance ids;
- `AuthContext`, caller permission/capability, and active membership checks;
- model policy and provider-secret boundaries;
- approval gates for external completion/failure, high-risk handoff, or side effects;
- audit/work traces for task creation, assignment, completion, failure, cancellation, dependency waits, and notification exposure;
- typed `worker.task.*` workstream events, attention state, and structured backend-projected surfaces for visible worker progress/results;
- provider/runtime fail-closed states and no fake success or model-less successful findings.

## Review checklist

- task result type is explicit for structured results;
- task rules reject invalid model output instead of accepting partial success;
- dependency cancellation behavior is understood;
- attachments cannot bypass tenant/customer or redaction boundaries;
- task queries and notification streams are authorization-protected;
- idempotency is defined for repeated task starts or external completions.
