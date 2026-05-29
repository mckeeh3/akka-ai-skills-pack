# Five Core Workstreams v0 Scope Plan

## Purpose

Create the coordination mini-project for turning the production-ready five-core v0 starter into a deliberately planned secure AI-first SaaS reference runtime, then hand off to one mini-project per core workstream.

This project plans the shared scope across:

1. My Account
2. User Admin
3. Agent Admin
4. Audit/Trace
5. Governance/Policy

The workstream-specific implementation queues live in sibling mini-projects:

- `specs/my-account-workstream-v0/`
- `specs/user-admin-workstream-v0/`
- `specs/agent-admin-workstream-v0/`
- `specs/audit-trace-workstream-v0/`
- `specs/governance-policy-workstream-v0/`

## Background

Existing completed queues already established the minimum and production-ready five-core v0 starter baseline:

- `specs/five-core-workstream-v0-starter/`
- `specs/production-ready-five-core-v0/`
- `specs/workstream-akka-agent-runtime/`

This project does not restart that baseline. It creates the next planning layer: a coherent v0 scope and dependency map for completing each core workstream vertically while demonstrating request/response AI agents, Akka `AutonomousAgent` components, and deterministic non-AI services in the right places.

## Scope

- Define common v0 acceptance standards for the five workstreams.
- Define the shared capability, security, managed-agent, trace, UI, and runtime validation contracts each workstream inherits.
- Identify cross-workstream dependencies and implementation order.
- Keep each workstream implementation in its own mini-project queue.
- Preserve the starter template as the executable/reference asset target unless a task explicitly narrows to docs or planning.

## Non-goals

- Do not implement workstream code in this planning mini-project.
- Do not complete every full-core SaaS surface.
- Do not add app-specific/domain-specific workstreams.
- Do not make AutonomousAgent the default for user-facing composer turns.

## Affected repository areas

- `templates/ai-first-saas-starter/`
- `docs/`
- `skills/`
- `specs/`
- `tools/validate-ai-first-saas-starter-fullstack.sh`
- root `frontend/` mirror where starter frontend changes must remain synchronized

## Execution model

Execute one task per fresh harness session. Start with this coordination queue, then work through the five workstream queues in dependency order.

Recommended order:

1. complete `specs/five-core-workstreams-v0-plan/` planning tasks;
2. execute `specs/my-account-workstream-v0/`;
3. execute `specs/user-admin-workstream-v0/`;
4. execute `specs/agent-admin-workstream-v0/`;
5. execute `specs/audit-trace-workstream-v0/`;
6. execute `specs/governance-policy-workstream-v0/`.

## Read order for future task sessions

1. `AGENTS.md`
2. `skills/README.md`
3. this mini-project's `README.md`
4. this mini-project's `conversation-capture.md`
5. this mini-project's `pending-tasks.md`
6. selected sprint/backlog/task brief
7. only the exact docs/skills/source files listed by the task

## Done state

This coordination mini-project is complete when:

- shared v0 scope across all five workstreams is captured;
- cross-workstream dependencies and acceptance gates are explicit;
- each workstream mini-project has a coherent first runnable task and terminal verification task;
- verification confirms the series can proceed one workstream at a time without re-planning all five.
