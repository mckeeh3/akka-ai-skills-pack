# Autonomous Agents Integration

## Purpose

Integrate Akka's newly released `AutonomousAgent` component into this skills-pack source repository as the canonical Akka substrate for durable background/internal agent work.

Autonomous Agents fit the pack's existing AI-first SaaS doctrine better than ad hoc background request/response agents because they provide durable typed tasks, model-driven iteration, task lifecycle, coordination capabilities, and notification streams. Request-based Akka `Agent` components remain the right fit for user-facing workstream turns and bounded request/response behavior.

## Source discussion

The user confirmed these decisions:

- include executable examples and tests under `src/` as part of the initiative;
- explicitly redefine background/internal agents around `AutonomousAgent` while preserving request-based `Agent` for workstream agents;
- switch generated-app guidance now, not merely document a future migration;
- integrate Autonomous Agents into the governed runtime model, authority boundaries, traces, model policy, and managed behavior artifacts;
- make Autonomous Agents the default for long-running background investigations, internal specialist agents, supervision/escalation processors, autonomous monitoring/remediation, batch/review/evaluation loops, and similar internal/background jobs;
- avoid over-correcting: not every agent should become autonomous;
- guard against naming collisions between Akka autonomous `AgentDefinition` and this pack's governed managed-agent `AgentDefinition` domain concept;
- every task in this mini-project must be self-contained, run in a fresh harness session, update the queue, and make one focused git commit.

## Scope

Affected areas include:

- `docs/` AI-first SaaS, agent workstream, capability-first, and agent coverage guidance;
- `skills/` routing and focused component skills for autonomous agents, tasks, coordination, notifications, governance, and testing;
- `skills/README.md` routing updates;
- `src/` executable reference examples and tests;
- `templates/ai-first-saas-starter/` guidance or implementation where internal/background agents are represented;
- `docs/agent-coverage-matrix.md` coverage tracking;
- possible `pack/` manifest/export updates only when new installable skills are added.

## Non-goals

- Do not replace user-facing workstream request/response agents with Autonomous Agents by default.
- Do not weaken governed runtime requirements: tenant isolation, backend authorization, tool boundaries, approval gates, provider-secret boundaries, and work traces remain mandatory.
- Do not treat official Akka Autonomous Agent coordination as a substitute for product-level capability modeling, authorization, audit, or supervision design.
- Do not create deterministic/demo/model-less normal runtime substitutes for generated apps.

## Required future read order

For every task, start with:

1. `AGENTS.md`
2. `skills/README.md`
3. `specs/autonomous-agents-integration/README.md`
4. `specs/autonomous-agents-integration/conversation-capture.md`
5. `specs/autonomous-agents-integration/pending-tasks.md`
6. the selected sprint, backlog, and task brief

Then read only the official Akka docs and local guidance listed by the selected task.

## Sprint sequence

1. **Research and classification** — deeply read official Akka Autonomous Agent docs and samples, then produce agent-optimized source notes.
2. **Doctrine and routing migration** — update core docs and routing so internal/background agent work defaults to `AutonomousAgent` when durable task/process semantics are needed.
3. **Skill family integration** — add focused installable skills and update manifest/routing.
4. **Executable examples and tests** — add minimal local examples proving single-agent task execution and coordination/testing patterns.
5. **Verification loop and follow-up planning** — review all changes for gaps, append follow-up tasks as needed, and define any additional example/test tasks once the first-pass migration is stable.

## Done state

This initiative is complete when:

- official Autonomous Agent semantics are captured in low-token, agent-optimized local guidance;
- routing clearly distinguishes request-based `Agent`, `AutonomousAgent`, `Workflow`, and `Workflow + Agent`;
- internal/background agents in generated-app doctrine and starter guidance default to Autonomous Agents where appropriate;
- governed runtime, capability-first, security, authorization, trace, model-policy, and tool-boundary requirements explicitly apply to Autonomous Agents;
- executable examples and tests exist under `src/` for the initial Autonomous Agent slice;
- `docs/agent-coverage-matrix.md` accurately records coverage and remaining gaps;
- the final verification loop has either closed gaps or appended follow-up tasks plus another verification task.
