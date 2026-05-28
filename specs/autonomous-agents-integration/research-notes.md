# Autonomous Agents Research Notes

## Sources read

Official Akka source material for this note:

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

An Akka `AutonomousAgent` is a model-driven durable process. It has no command handlers. The component class extends `AutonomousAgent`, has a mandatory `@Component(id = ..., description = ...)`, and implements one `definition()` method returning Akka autonomous `AgentDefinition`.

The runtime owns the loop:

1. an external caller creates or assigns a typed task via `ComponentClient`;
2. the runtime starts or resumes an autonomous-agent instance;
3. the model iterates, calling domain tools, component tools, MCP tools, or coordination tools;
4. the task reaches a terminal state when the model calls built-in completion/failure semantics, a task rule rejects/retries until accepted, or the iteration limit fails it;
5. task and agent state survive crashes and restarts.

Important terminology for this pack: Akka autonomous `AgentDefinition` is an SDK builder/definition type returned by `AutonomousAgent.definition()`. It is not the same thing as this skills pack's governed managed-agent `AgentDefinition` domain concept used for tenant-scoped behavior/profile governance. Future guidance should spell this out whenever both appear.

## Minimum component shape

```java
@Component(
  id = "question-answerer",
  description = "Answers questions clearly and concisely, showing reasoning step by step"
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
- `TaskTemplate<R>`: parameterized task definition using placeholders such as `{feature}`. Use `params(Map<String, String>)` to resolve parameters or `instructions(String)` to discard the template and provide free-form instructions. Templates are accepted by `TaskAcceptance.of(...)`, delegation, and team task lists.
- `TaskAcceptance`: capability declaring task types an agent can process. `TaskAcceptance.of(TASK...)` can set `maxIterationsPerTask(n)` and `canHandoffTo(...)`.
- `TaskRule<R>`: validates a structured result before completion. `Result.Rejected` moves the task to `RESULT_REJECTED`; the runtime injects the rejection reason on the next model iteration. Unsatisfied rules eventually fail by iteration limit.
- Attachments: task instances can attach text, images, PDFs, or URI-backed content. Built-in `http(s)://` and object-storage references are resolved by the runtime; custom sources need a static `ContentLoader` in the definition.
- Dependencies: `task.dependsOn(taskId)` delays execution until all dependencies complete. Failed/cancelled dependencies cancel dependents.

Task lifecycle statuses:

- `PENDING`
- `ASSIGNED`
- `IN_PROGRESS`
- `RESULT_REJECTED`
- `COMPLETED`
- `FAILED`
- `CANCELLED`

Terminal states are `COMPLETED`, `FAILED`, and `CANCELLED`. A failed task has no automatic retry; application code creates a new task if retry is desired.

## Definition API concepts

`define()` builds Akka autonomous `AgentDefinition` with:

- `instructions(...)` for tone, role, domain rules, or procedural prompt guidance;
- `.capability(TaskAcceptance.of(...))` for accepted tasks;
- `.capability(Delegation.to(...))` for delegative coordination;
- `.capability(TeamLeadership.of(...))` for collaborative teams;
- `.capability(Moderation.of(...))` for turn-taking conversations;
- task handoff via `TaskAcceptance.of(...).canHandoffTo(...)`;
- `.tools(...)` for separate tool instances/classes or Akka components as tools;
- `@FunctionTool` methods directly on the agent class;
- `.mcpTools(...)` for remote MCP endpoints;
- request/response guardrails;
- `.modelProvider(...)` for per-agent model selection;
- `.contentLoader(...)` for resolving custom task attachments.

Dynamic configuration uses `AgentSetup.create()` and `componentClient.forAutonomousAgent(...).setup(...)` before `runSingleTask` or `assignTasks`. It can override instance instructions and capabilities only. It cannot override `@Component` description, static tools, model provider, guardrails, or content loader.

## ComponentClient API

Start one auto-stopping task instance:

```java
String taskId = componentClient
  .forAutonomousAgent(QuestionAnswerer.class, agentInstanceId)
  .runSingleTask(QuestionTasks.ANSWER.instructions(question));
```

`runSingleTask` returns immediately with the durable task id. It starts an independent agent instance and auto-stops it when its queue drains, including spawned subtasks.

Manage tasks separately:

```java
String taskId = componentClient
  .forTask(UUID.randomUUID().toString())
  .create(MyTasks.COLLECT.instructions("Collect data"));

componentClient
  .forAutonomousAgent(ReportAgent.class, agentInstanceId)
  .assignTasks(taskId);
```

When using `assignTasks`, application code must eventually `terminate()`, `suspend()`, or otherwise manage the agent lifecycle; it does not auto-stop just because the queue is empty.

Task operations:

- `forTask(taskId).create(task)`
- `forTask(taskId).get(taskDefinition)` returns a snapshot with status, result, and failure reason
- `forTask(taskId).result(taskDefinition)` waits for terminal completion and returns typed result or throws `TaskException.Failed` / `TaskException.Cancelled`
- `forTask(taskId).assign(owner)`
- `forTask(taskId).complete(taskDefinition, result)`
- `forTask(taskId).fail(reason)`
- `forTask(taskId).notificationStream()` for terminal task notifications

Agent operations:

- `forAutonomousAgent(AgentClass.class, instanceId).setup(AgentSetup)`
- `runSingleTask(task)`
- `assignTasks(taskIds...)`
- `getState()` exposes phase, suspended flag, instructions, token usage, current task, and pending task ids
- `suspend()` / `resume()`
- `terminate()`
- `notificationStream()` for live agent notifications

Async variants exist for the same operations, returning `CompletionStage`.

## Coordination model

Autonomous Agent coordination is model-driven. Declared capabilities become built-in tools exposed to the model. The model decides whether and when to use them during the loop.

### Decomposition unit rules

Choose the smallest abstraction that preserves semantics:

- Tool: quick lookup, deterministic computation, or component capability used inside the same model iteration; no lifecycle of its own.
- Task: typed result with identity, lifecycle, dependencies, external observation, external completion, or independent success/failure.
- Separate agent: distinct purpose, focused context, distinct model, isolation, delegation/handoff/team/moderation boundary.

Delegation targets can be request-based `Agent` or `AutonomousAgent`:

- Use request-based `Agent` as a delegated worker for one-shot prompt/tool work whose result should stay outside the coordinator context.
- Use `AutonomousAgent` when the worker benefits from its own iteration loop, durable task lifecycle, tool-driven investigation, parallel workers, handoff, or observability.

### Delegation

`Delegation.to(Worker.class...)` enables fan-out/fan-in. The runtime creates subtasks, starts worker instances, assigns tasks, waits for typed results, and returns results to the coordinator's tool loop. `maxParallelWorkers(n)` limits concurrent delegated workers for that capability. Declare multiple `Delegation` capabilities when worker groups need different parallelism budgets.

### Handoff

`TaskAcceptance.of(TASK).canHandoffTo(Target.class...)` transfers the current task to another agent. Ownership moves to the target; the source agent is done. Handoff is appropriate for triage/routing and sequential specialist stages. Handoff targets usually accept the same task type.

### TeamLeadership

`TeamLeadership.of(TeamMember.of(Member.class).maxInstances(n))` lets a lead form a team with a shared backlog. Members claim tasks, work independently, send messages, and complete tasks. The lead monitors the shared task list and disbands the team. Use for interdependent work where peer messages or collaborative task claiming help.

### Moderation

`Moderation.of(Participant.class...)` lets a moderator coordinate turn-taking conversations. Participants can have near-empty definitions with only meaningful `@Component` descriptions. Modes include scripted conversations and directed conversations. Use for peer review, negotiations, panel-style analysis, or structured debate.

### External input

External/human input is modeled as an unassigned task in a dependency chain, not as a special capability. Application code creates the task, later `assign`s it to the human/process and `complete`s or `fail`s it. Downstream tasks wait on dependency completion or are cancelled on failure.

## Notifications

Two notification families exist:

- Agent notifications: `akka.javasdk.agent.autonomous.Notification`, emitted by an agent instance and observed through `forAutonomousAgent(...).notificationStream()`.
- Task entity notifications: `akka.javasdk.agent.task.TaskNotification`, emitted by task entities on terminal transitions and observed through `forTask(taskId).notificationStream()`.

Agent notification families:

- lifecycle: activated, deactivated, iteration started/completed/failed, suspended, resumed, stopped;
- task: assigned, started, result rejected, completed, failed, cancelled, dependency wait/resolved;
- handoff: handoff started/received;
- delegation: delegation started/resolved, worker task received/completed;
- team: team created/member ready/setup failed/member stopped/disbanded/joined;
- conversation: conversation created/participant ready/setup failed/ended/turn received/joined/turn submitted;
- messaging: message received/contact added;
- struggle: task struggle detected, dependency stuck, approaching max iterations, repeated iteration failure.

Notifications are useful for dashboards, logs, progress UIs, and tests, but not as the source of truth for business correctness. Use task snapshots or agent state for reliable decisions.

## Testing facts

AutonomousAgent tests use `TestKitSupport`, `TestModelProvider`, and Awaitility because execution is asynchronous.

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

Use `TestModelProvider.AutonomousAgentTools` for built-in tool invocations instead of raw tool-name strings:

- `completeTask(result)`
- `completeTaskJson(json)`
- `failTask(reason)`
- `handoffTo(Target.class, context)`
- `delegateTo(taskDefinition, Worker.class, instructions)`
- `delegateTo(RequestBasedAgent.class, jsonPayload)`
- team/backlog/messaging/moderation helpers such as `createTeam`, `claimTask`, `sendMessage`, `startScriptedConversation`, `submitTurn`, etc.

For domain tools, raw `TestModelProvider.ToolInvocationRequest` still uses the actual tool name exposed from `@FunctionTool` methods, such as `ConsultingTools_assessProblem`.

Recommended test assertions:

- trigger through endpoint or `ComponentClient` path, not by directly invoking fake services;
- poll `componentClient.forTask(taskId).get(TASK)` until `snapshot.result()` exists or status is terminal;
- assert typed result fields and failure reasons;
- for coordination, register each agent's model provider and script coordinator/worker turns separately;
- use `whenMessage(...)` and `whenToolResult(...)` for multi-iteration scripts;
- call `model.reset()` in `@AfterEach` when providers are reused across tests.

## Agent vs AutonomousAgent vs Workflow

### Use request-based `Agent` when

- the interaction is request/response, including user-facing workstream turns;
- the caller expects an immediate or streamed response for a bounded turn;
- you need fine-grained prompt/effects control per call;
- a Workflow step needs one model round trip;
- session memory/conversational context is the main persistence need;
- the work can be modeled as a normal component call rather than a durable typed task.

### Use `AutonomousAgent` when

- the agent should run as a durable background/internal process;
- work is open-ended or investigative and the model chooses what to do next;
- the work has a typed task result that outlives the initiating request;
- tasks need independent lifecycle, dependencies, failure/cancellation, snapshots, or external completion;
- model-driven multi-agent coordination is needed: delegation, handoff, teams, moderation;
- monitoring, batch review, escalation processing, internal investigations, remediation, or evaluator loops should continue outside a single user-facing turn.

### Use `Workflow` when

- the sequence of steps is deterministic and explicit;
- approval, retry, compensation, timeout, or deterministic orchestration is the key requirement;
- the model is consulted at most once inside each explicit step;
- product correctness depends on fixed order rather than model-chosen iteration.

### Use Workflow + Agent when

- a business process is deterministic but one or more steps need bounded LLM reasoning;
- the Workflow owns retries, compensation, approvals, and external state transitions;
- each Agent invocation is a single request/response step.

### Use Workflow + AutonomousAgent when

- a deterministic business process needs to launch or wait on a durable model-driven investigation/task;
- the Workflow owns product process state, while the AutonomousAgent owns the open-ended task loop;
- human approval or external input should be modeled as task dependencies if it is inside the autonomous task graph, or as workflow pause/resume if it belongs to broader business orchestration.

## Governance implications for this pack

Autonomous Agents add durable agent/task machinery but do not replace the pack's governed runtime requirements. Generated-app guidance should require the same controls before exposing Autonomous Agents to tenant/user work:

- capability-first contracts for every task start, task query, task completion/failure, tool call, handoff, delegation, team action, moderation action, and notification exposure;
- backend authorization for `runSingleTask`, `assignTasks`, `forTask(...).get/result`, external `complete/fail`, `suspend/resume/terminate`, and notification streams;
- tenant/customer scoping in task ids, agent instance ids, task instructions, attachments, tool arguments, and query filters;
- model policy and provider-secret boundaries for every autonomous agent class and dynamic setup path;
- `ToolPermissionBoundary` enforcement before registering function/component/MCP tools or governed `readSkill` / `readReferenceDoc` loader tools;
- approval gates for side-effecting tools, external task completion, handoff to higher-authority agents, policy changes, or remediation actions;
- durable traces for prompt assembly, skill/reference loads, task creation/assignment/completion/failure, tool invocation, authorization denial, delegation/handoff/team/moderation events, notification exposure, and human external-input decisions;
- fail-closed behavior when model/provider/security configuration is absent;
- no deterministic/demo/model-less normal runtime substitute for user-facing or internal generated-app behavior.

Mapping to governed managed-agent concepts:

- pack-level governed managed-agent `AgentDefinition` should probably configure or select an Akka autonomous agent class/role, task definitions, static/default instructions, model policy, skill/reference manifests, tool boundaries, and approval rules;
- Akka autonomous `AgentDefinition` remains the SDK static/dynamic component definition returned by `definition()` and optionally `AgentSetup`;
- generated-app docs must avoid saying simply "AgentDefinition" in autonomous contexts without qualifying which one.

## Local example targets

Good first executable examples under `src/`:

1. Single-agent typed task:
   - `AnswerQuestionAgent extends AutonomousAgent`
   - `Task<Answer>` with `Answer` record
   - endpoint or test starts with `ComponentClient.forAutonomousAgent(...).runSingleTask(...)`
   - test uses `AutonomousAgentTools.completeTask(new Answer(...))`
   - Awaitility polls `forTask(taskId).get(TASK)`.

2. Task rule failure/retry:
   - task result has a required field such as `sources`;
   - `TaskRule` rejects missing/empty sources;
   - test scripts first rejected result then accepted result or verifies final failure.

3. Delegation:
   - coordinator accepts top-level `Task<Brief>`;
   - workers accept `Task<Findings>` and `Task<Analysis>`;
   - coordinator declares `Delegation.to(Researcher.class, Analyst.class)`;
   - test uses `AutonomousAgentTools.delegateTo(...)` and worker `completeTask(...)`.

4. Handoff / external input follow-up:
   - triage hands off same task to specialist;
   - or human approval task is created unassigned between draft and publish tasks.

Keep examples small and focused. Do not wire generated-app governance as a fake runtime substitute; governance-heavy examples should be explicit reference slices or test-only adapters until the real governed runtime path exists.
