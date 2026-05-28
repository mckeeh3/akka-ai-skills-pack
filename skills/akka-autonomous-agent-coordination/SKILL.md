---
name: akka-autonomous-agent-coordination
description: Implement Autonomous Agent delegation, handoff, TeamLeadership, Moderation, task dependencies, external input, and notification-aware coordination.
---

# Akka Autonomous Agent Coordination

Use this skill when an Autonomous Agent coordinates with other agents, tasks, teams, conversations, or external/human input.

## Required reading

Read when API details are needed:
- `../../docs/agent-component-selection-guide.md`
- `../../specs/autonomous-agents-integration/research-notes.md`
- `akka-context/sdk/autonomous-agents/coordination.html.md`
- `akka-context/sdk/autonomous-agents/capabilities.html.md`
- `akka-context/sdk/autonomous-agents/notifications.html.md`
- `akka-context/sdk/autonomous-agents/testing.html.md`

## Coordination choice

Choose the smallest abstraction that preserves semantics:

| Need | Prefer |
|---|---|
| quick lookup, deterministic computation, or component call inside one model iteration | tool |
| typed work with identity, lifecycle, dependencies, observation, or external completion | task |
| distinct purpose, model, isolation, loop, or coordination boundary | separate agent |
| fixed business sequence, retry, approval, compensation, timeout | Workflow |

## Patterns

### Delegation

Use `Delegation.to(Worker.class...)` when the coordinator should fan out subtasks and receive typed results while keeping ownership of the top-level task. Use `maxParallelWorkers(n)` to cap concurrency.

Delegation targets may be request-based `Agent` classes for one-shot prompt/tool work or `AutonomousAgent` classes for durable worker loops.

### Handoff

Use `TaskAcceptance.of(TASK).canHandoffTo(Target.class...)` when ownership of the current task should transfer to a specialist. Handoff targets usually accept the same task type.

### TeamLeadership

Use `TeamLeadership.of(TeamMember.of(Member.class).maxInstances(n))` for shared-backlog team work where members claim tasks, exchange messages, and the lead monitors progress.

### Moderation

Use `Moderation.of(Participant.class...)` for turn-taking conversations such as debates, panels, negotiations, or structured reviews.

### External input

Model human/external input as an unassigned task in a dependency chain. Application code creates the task, later assigns it to the human/process, and completes or fails it. Downstream tasks wait on completion.

## Notifications

Use notifications for dashboards, logs, progress UI, and tests:
- agent notifications: lifecycle, iteration, task, handoff, delegation, team, conversation, messaging, struggle;
- task notifications: terminal task transitions.

Do not treat notifications as the source of truth for business correctness; use task snapshots or component state for decisions.

## Generated SaaS guardrails

- Every coordination operation is a capability: delegation, handoff, team creation, moderation, task assignment, external completion, and notification streaming.
- Handoff to higher-authority agents, side-effecting workers, or broader data scope requires approval or explicit bounded policy.
- Preserve tenant/customer scope in task ids, instance ids, instructions, attachments, worker inputs, and notification filters.
- Emit traces for delegation/handoff/team/moderation events, authorization denials, tool use, and external input decisions.
- Do not let prompt text, task instructions, or worker output expand authority; backend policies and `ToolPermissionBoundary` remain authoritative.

## Review checklist

- coordination adds value over one agent, one task, or a Workflow;
- delegation/handoff targets have explicit accepted task types;
- concurrency limits are set when fan-out can grow;
- external input is modeled as tasks or Workflow pause/resume intentionally;
- task/result DTOs and traces are tenant-scoped and redacted;
- tests script coordinator and worker model providers separately.
