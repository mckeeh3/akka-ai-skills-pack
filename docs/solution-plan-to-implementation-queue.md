# Solution plan to implementation queue

Use this lightweight template after Stage 1 decomposition is accepted.

For durable multi-session execution with task status, use `pending-question-queue.md` for unresolved decisions and `pending-task-queue.md` for implementation work. Materialize them as `specs/pending-questions.md` and `specs/pending-tasks.md`.

Purpose:
- turn unresolved design decisions into a durable clarification queue when needed
- preserve full-stack secure AI-first SaaS operating-model context, including mandatory foundation, UI surfaces, governance, supervision, audit, and outcomes
- turn the solution plan into a downstream implementation work queue
- keep coding focused on one bounded vertical increment at a time
- make code generation, test generation, local runtime validation, and manual/smoke verification explicit follow-on work

## Rule

A solution plan is not the final output.
It is the implementation contract for downstream work.

That contract should tell the next agent or next phase:
- what fully working app state each increment should produce
- in what order to build increments
- which functional agent, workstream attention category, dashboard summary, surface/action, workstream event, or internal trigger owns each increment
- which governed capability id/class, AuthContext, authorization rule, approval, notification/projection behavior, audit/trace, and exposure channel each increment implements
- which Akka substrate and frontend/API/realtime files are expected
- whether durable internal/background model-driven work should be an AutonomousAgent task with lifecycle, result, notification, dependency/failure/cancellation, and tool-authority semantics
- which skills to load for each build step
- which tests and local validation paths prove the increment works
- which delegated authority, policy/approval, trace, supervision UI, evaluation, and outcome requirements must be carried into each generated SaaS task
- which workstream expertise requirements each functional-agent task must preserve: expert bundle id, prompt, skills, references, compact manifests, loader tools, tool boundaries, seed/import behavior, governance UI, traces, and tests

## Done means working

For generated Akka apps, especially full-stack secure AI-first SaaS apps, do not treat a queue item or sprint as complete merely because a component was generated. A named feature such as `user sign-in`, `user auth`, `invitation onboarding`, `User Admin`, `Agent Admin`, or an app-specific workstream is implemented only when the required backend, API, frontend/workstream surface, authorization, audit/trace, and tests needed for that named scope work together.

Deferrals are allowed only when they narrow or rename the goal, are marked as blocked/deferred in the queue, or are outside the explicitly selected scope. If a deferral prevents the named feature from working through the locally running Akka app, the feature is not done.

## Minimal transformation

Take these sections from the solution plan:
- AI-first interpretation: delegated work, retained authority, durable objects, policy/approval/exception needs, traces, mandatory UI surfaces, and outcomes
- functional agents, internal agents, workstream attention categories, dashboard contracts, workstream events, structured surfaces, and surface actions
- workstream expert bundles for functional agents with LLM behavior: prompt intent, `SkillDocument` and `ReferenceDocument` families, `AgentSkillManifest` and `AgentReferenceManifest`, `ToolPermissionBoundary`, `readSkill`/`readReferenceDoc`, `SkillLoadTrace`/`ReferenceLoadTrace`, seed/import behavior, governance UI, and tests
- governed capability inventory: ids/classes, actors/callers, AuthContext/scope, schemas, side effects, idempotency, approval, audit/trace, exposure channels, and tests
- chosen Akka substrate and frontend/API/realtime outputs
- AutonomousAgent task candidates for durable internal/background model-driven work, including task lifecycle, result surfaces, notifications, dependencies, failure/cancellation, and authorization/tool-boundary constraints
- events, notifications, projections, left-rail/My Account attention summaries, and audit/work trace obligations
- skill routing
- recommended implementation order
- required tests and local validation paths

Then convert them into a vertical queue like this:

```md
# Implementation Queue

1. <Foundation or feature vertical>: <working state>
   - goal: user can <exercise visible/API/workstream behavior> in the locally running Akka app
   - functional agent / surface / trigger: <agent + attention category/dashboard/surface action, workstream event, or internal-only foundation scope>
   - capability: <capability id/class>
   - expertise scope: <none, or expert bundle/prompt/skills/references/manifests/boundaries/loaders/UI/tests>
   - auth/scope: <AuthContext, role/capability, tenant/customer rules>
   - Akka substrate: <entity/workflow/view/consumer/timer/agent/autonomous task/endpoint>
   - notifications/projections/traces: <events, notification stream, dashboard/attention projection, audit/work trace>
   - frontend/API/realtime: <UI/API/client/stream work, or non-UI reason>
   - outputs: <files or file families>
   - skills: <focused skills>
   - required checks: <unit/integration/frontend/security/audit checks>
   - local validation: <Akka run, endpoint smoke, browser/workstream action, or manual checklist>
   - done means: <observable behavior that proves this increment works>

2. <Next vertical increment>: <working state>
   - ...
```

Use component family names to describe implementation files, not to define the task boundary. Avoid queues like `all domain`, `all entities`, `all views`, then `all UI` unless the item is explicitly an internal prerequisite and its done criteria state why it is not a user-visible increment. Avoid `make the agent expert` or `agent governance` queue items unless they are split into self-contained fresh-session tasks for expert bundle definition, prompt/skill/reference content, manifests, tool boundaries, authorized loaders, UI/governance, traces, and tests.

## Practical use

For each queue item:
1. load only the listed skills
2. generate or update the code for that bounded vertical increment
3. generate its corresponding tests before moving on
4. run the required checks and local validation path when the task implements runtime behavior
5. preserve only the AI-first context needed for that queue item; do not reread or duplicate the full doctrine unless the task needs it
6. keep later increments out of context until their step begins

For reliable follow-on work across sessions, first convert unresolved blocking decisions into `specs/pending-questions.md` and answer them with `akka-do-next-pending-question`. Then convert unblocked implementation work into `specs/pending-tasks.md` and execute it with `akka-do-next-pending-task` one task at a time.

When requirements evolve after the queue exists:
- use `akka-change-request-to-spec-update` for bounded feature requests, bugs, issues, or implementation discoveries
- use `akka-revised-prd-reconciliation` for revised/replacement PRDs
- use `akka-pending-question-queue-maintenance` to audit stale, duplicate, blocked, answered-but-unreconciled, or superseded question entries
- use `akka-pending-task-queue-maintenance` to audit stale, duplicate, blocked, or superseded task entries

## What belongs downstream

The downstream implementation phase may include:
- backend component generation
- endpoint generation
- web UI generation
- test generation
- local run, endpoint smoke, browser/workstream smoke, or manual verification notes
- documentation or snippet generation when the task asks for it

## Quick checklist

Before starting code generation, verify that the solution plan already answers:
- what working app state this task or sprint should produce
- which functional agent, attention/dashboard, surface action, workstream event, governed capability, or internal trigger owns the work
- which component/files are first
- which skills implement them
- which tests belong with them
- which local run/manual smoke path proves the named behavior works
- which later increments depend on this one
- whether any open questions still block coding
- whether blocking questions are resolved or explicitly deferred in `specs/pending-questions.md`
- whether any explicit deferral narrows the feature goal instead of being counted as completed work
- whether AI-first authority, policy, approval, trace, UI, evaluation, or outcome decisions are represented in the relevant queue items instead of silently dropped
- whether durable background model-driven work is represented as an AutonomousAgent task candidate when lifecycle, result, notification, dependency, failure/cancellation, or handoff semantics fit
- whether every new or materially changed functional agent has explicit workstream expertise tasks for prompts, skills, references, manifests, boundaries, loaders, seed/import behavior, UI/governance surfaces, and assigned/denied/boundary/trace tests

## Related docs

- `pending-question-queue.md`
- `pending-task-queue.md`
- `intent-driven-usage-flow.md`
- `prd-to-akka-flow.md`
- `module-sprint-planning.md`
- `examples/purchase-request-solution-plan.md`
- `examples/purchase-request-pending-tasks.md`
- `../skills/README.md`
- `../skills/akka-solution-decomposition/SKILL.md`
- `../skills/akka-pending-question-generation/SKILL.md`
- `../skills/akka-do-next-pending-question/SKILL.md`
- `../skills/akka-pending-question-queue-maintenance/SKILL.md`
- `../skills/akka-backlog-to-pending-tasks/SKILL.md`
- `../skills/akka-change-request-to-spec-update/SKILL.md`
- `../skills/akka-revised-prd-reconciliation/SKILL.md`
- `../skills/akka-pending-task-queue-maintenance/SKILL.md`
- `../skills/akka-do-next-pending-task/SKILL.md`
