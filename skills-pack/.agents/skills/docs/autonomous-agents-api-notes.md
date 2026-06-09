# Autonomous Agents API Notes

This installed pack note provides durable Akka Autonomous Agent API details for skills-pack users when the official `akka-context/**` files are unavailable or too broad.

## Sources

Official Akka source material summarized here:

- `akka-context/sdk/autonomous-agents.html.md`
- `akka-context/sdk/autonomous-agents/defining.html.md`
- `akka-context/sdk/autonomous-agents/tasks.html.md`
- `akka-context/sdk/autonomous-agents/coordination.html.md`
- `akka-context/sdk/autonomous-agents/capabilities.html.md`
- `akka-context/sdk/autonomous-agents/client.html.md`
- `akka-context/sdk/autonomous-agents/notifications.html.md`
- `akka-context/sdk/autonomous-agents/testing.html.md`
- `akka-context/sdk/use-cases/autonomous-agents.html.md`

## Core model

An Akka `AutonomousAgent` is a model-driven durable process. It has no command handlers. The component class extends `AutonomousAgent`, has a mandatory `@Component(id = ..., description = ...)`, and implements one `definition()` method returning the SDK autonomous-agent `AgentDefinition`.

The runtime owns the loop: a caller creates or assigns a typed task, the model iterates through tools and coordination capabilities, and the task reaches a terminal state through completion, failure, cancellation, rule rejection exhaustion, or iteration-limit failure. Task and agent state survive crashes and restarts.

Important terminology: the Akka autonomous-agent `AgentDefinition` returned by `AutonomousAgent.definition()` is an SDK definition. It is not the same as this pack's tenant-scoped governed managed-agent `AgentDefinition` domain concept.

## Minimum component shape

```java
@Component(
  id = "question-answerer",
  description = "Answers questions clearly and concisely"
)
public class QuestionAnswerer extends AutonomousAgent {
  @Override
  public AgentDefinition definition() {
    return define()
      .capability(TaskAcceptance.of(QuestionTasks.ANSWER).maxIterationsPerTask(3));
  }
}
```

Task definitions are usually static constants:

```java
public static final Task<Answer> ANSWER = Task
  .name("Answer")
  .description("Answer a question")
  .resultConformsTo(Answer.class);
```

If `resultConformsTo` is omitted, the result defaults to `String`.

## Task API concepts

- `Task<R>`: immutable task definition and per-request task instance builder. Defines name, description, result type, optional rules, instructions, attachments, and dependencies.
- `TaskTemplate<R>`: parameterized task definition using placeholders. Use `params(Map<String, String>)` to resolve parameters or `instructions(String)` to discard the template and provide free-form instructions.
- `TaskAcceptance`: capability declaring task types an agent can process. `TaskAcceptance.of(TASK...)` can set `maxIterationsPerTask(n)` and `canHandoffTo(...)`.
- `TaskRule<R>`: validates a structured result before completion. `Result.Rejected` moves the task to `RESULT_REJECTED`; the runtime injects the rejection reason on the next model iteration.
- Attachments: task instances can attach text, images, PDFs, or URI-backed content. Built-in `http(s)://` and object-storage references are resolved by the runtime; custom sources need a static `ContentLoader` in the definition.
- Dependencies: `task.dependsOn(taskId)` delays execution until all dependencies complete. Failed or cancelled dependencies cancel dependents.

Task statuses: `PENDING`, `ASSIGNED`, `IN_PROGRESS`, `RESULT_REJECTED`, `COMPLETED`, `FAILED`, `CANCELLED`. Terminal states are `COMPLETED`, `FAILED`, and `CANCELLED`.

## Definition API concepts

`define()` builds the Akka autonomous-agent SDK definition with:

- `instructions(...)` for tone, role, domain rules, or procedural guidance;
- `.capability(TaskAcceptance.of(...))` for accepted tasks;
- `.capability(Delegation.to(...))` for delegative coordination;
- `.capability(TeamLeadership.of(...))` for collaborative teams;
- `.capability(Moderation.of(...))` for turn-taking conversations;
- task handoff through `TaskAcceptance.of(...).canHandoffTo(...)`;
- `.tools(...)` for separate tool instances/classes or Akka components as tools;
- `@FunctionTool` methods directly on the agent class;
- `.mcpTools(...)` for remote MCP endpoints;
- request/response guardrails;
- `.modelProvider(...)` for per-agent model selection;
- `.contentLoader(...)` for custom task attachment resolution.

Dynamic configuration uses `AgentSetup.create()` and `componentClient.forAutonomousAgent(...).setup(...)` before `runSingleTask` or `assignTasks`. It can override instance instructions and capabilities only; it cannot override `@Component` description, static tools, model provider, guardrails, or content loader.

## ComponentClient API

Start one auto-stopping task instance:

```java
String taskId = componentClient
  .forAutonomousAgent(QuestionAnswerer.class, agentInstanceId)
  .runSingleTask(QuestionTasks.ANSWER.instructions(question));
```

`runSingleTask` returns immediately with the durable task id and auto-stops the agent instance when its queue drains.

Manage tasks separately:

```java
String taskId = componentClient
  .forTask(UUID.randomUUID().toString())
  .create(MyTasks.COLLECT.instructions("Collect data"));

componentClient
  .forAutonomousAgent(ReportAgent.class, agentInstanceId)
  .assignTasks(taskId);
```

With `assignTasks`, application code must eventually manage lifecycle through `terminate()`, `suspend()`, or another explicit operation.

Useful task operations include `create`, `get`, `result`, `assign`, `complete`, `fail`, and `notificationStream`. Useful agent operations include `setup`, `runSingleTask`, `assignTasks`, `getState`, `suspend`, `resume`, `terminate`, and `notificationStream`. Async variants return `CompletionStage`.

## Coordination model

Autonomous-agent coordination is model-driven. Declared capabilities become built-in tools exposed to the model. The model decides whether and when to use them during the loop.

Choose the smallest abstraction that preserves semantics:

- Tool: quick lookup, deterministic computation, or component capability inside the same model iteration.
- Task: typed result with identity, lifecycle, dependencies, observation, external completion, or independent failure semantics.
- Separate agent: distinct purpose, focused context, distinct model, isolation, delegation/handoff/team/moderation boundary.

Coordination capabilities:

- Delegation: `Delegation.to(Worker.class...)` enables fan-out/fan-in, with optional parallelism limits.
- Handoff: `TaskAcceptance.of(TASK).canHandoffTo(Target.class...)` transfers current task ownership.
- TeamLeadership: `TeamLeadership.of(TeamMember.of(Member.class).maxInstances(n))` forms a shared backlog team.
- Moderation: `Moderation.of(Participant.class...)` coordinates turn-taking conversations.
- External input: model as an unassigned task in a dependency chain, then complete or fail it externally.

## Notifications

Agent notifications are observed through `forAutonomousAgent(...).notificationStream()`. Task notifications are observed through `forTask(taskId).notificationStream()`.

Notifications are useful for dashboards, logs, progress UIs, and tests, but not as the source of truth for business correctness. Use task snapshots or agent state for reliable decisions.

## Testing facts

Autonomous-agent tests use `TestKitSupport`, `TestModelProvider`, and Awaitility because execution is asynchronous.

Register one provider per agent class:

```java
private final TestModelProvider model = new TestModelProvider();

@Override
protected TestKit.Settings testKitSettings() {
  return TestKit.Settings.DEFAULT
    .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
    .withModelProvider(QuestionAnswerer.class, model);
}
```

Use `TestModelProvider.AutonomousAgentTools` for built-in tool invocations such as `completeTask`, `completeTaskJson`, `failTask`, `handoffTo`, `delegateTo`, team, messaging, and moderation helpers. For domain tools, raw `TestModelProvider.ToolInvocationRequest` still uses the actual tool name exposed from `@FunctionTool` methods.

Recommended assertions:

- trigger through endpoint or `ComponentClient` path, not by directly invoking fake services;
- poll `componentClient.forTask(taskId).get(TASK)` until result exists or status is terminal;
- assert typed result fields and failure reasons;
- register each coordinator/worker model provider for coordination tests;
- use `whenMessage(...)` and `whenToolResult(...)` for multi-iteration scripts;
- call `model.reset()` in `@AfterEach` when providers are reused.

## Selection guidance

Use request-based `Agent` for user-facing request/response turns, immediate or streamed bounded replies, workflow steps needing one model round trip, or session-memory-centered conversational context.

Use `AutonomousAgent` for durable background/internal task work, open-ended investigation, task result lifecycle, dependencies, failure/cancellation, monitoring, batch review, remediation, evaluator loops, and model-driven coordination.

Use `Workflow` for deterministic steps, approval, retry, compensation, timeout, and explicit orchestration.

Use Workflow + AutonomousAgent when a deterministic business process launches or waits on a durable model-driven investigation.

## Governance implications for this pack

Autonomous Agents do not replace generated-app governance. Require capability-first contracts, backend authorization, tenant/customer scoping, model policy, provider-secret boundaries, `ToolPermissionBoundary`, approval gates, durable traces, provider/security fail-closed behavior, and no deterministic/demo/model-less normal runtime substitute.

Generated-app docs must qualify which `AgentDefinition` is meant: Akka autonomous-agent SDK definition or pack-level governed managed-agent definition.
