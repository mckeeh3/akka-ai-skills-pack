# Workstream Surface Intent Routing

## Purpose

Create a durable mini-project for making the runnable secure AI-first SaaS core app route workstream composer requests to fast, backend-authorized, pre-populated surfaces before falling back to model-backed workstream chat.

The goal is to make requests such as `create organization "Org 1"` open the appropriate User Admin Organization Create surface with safe prefilled fields instead of asking the User Admin agent to perform a direct mutation or waiting for a model response.

## Source discussion / trigger

A browser screenshot showed an authorized user asking the User Admin workstream to `create organization "Org 1"`. The response refused because the free-form User Admin agent chat is currently governed as read/advisory, while Organization creation is available through structured backend-authorized surface actions. The follow-up discussion concluded:

- workstream agents should be familiar with their workstream surfaces;
- free-form chat should not directly submit side-effecting commands for now;
- deterministic routing to pre-populated surfaces is safer, faster, and trains users to use the structured workstream surfaces;
- direct agent command submission can remain out of scope until a later, separately governed initiative.

## Scope

This mini-project targets root app-facing assets:

- `src/main/java/ai/first/application/coreapp/workstream/**`
- `src/main/java/ai/first/application/foundation/agent/**` where governed seed/reference familiarity needs updating
- `src/test/java/ai/first/**`
- `frontend/src/workstream/**`
- `frontend/src/api/**`
- `frontend/src/**/*.contract.test.mjs`
- `app-description/**` and/or `specs/**` contract docs when needed

## Done state

This mini-project is complete when all five core workstreams use a deterministic surface intent routing path before model-backed chat at the stated scope:

- the composer/backend attempts deterministic surface routing before invoking the model-backed workstream agent;
- matched requests open the correct authorized surface quickly and never submit side-effecting commands;
- supported create/edit/task surfaces can receive safe prefilled fields from the router;
- User Admin Organization Admin routing handles `create organization "Org 1"` by opening the Organization Create surface with `organizationName = Org 1`;
- each core workstream has a small surface catalog covering surface purpose, prompt examples, required capabilities, prefill fields, and forbidden direct effects;
- workstream agent prompt/skill/reference seed material is updated so agents can explain and recommend their own surfaces accurately;
- unmatched or ambiguous prompts still fall back to the governed model-backed chat path and fail closed when provider/runtime configuration is unavailable;
- routing, authorization, no-mutation behavior, safe denials, traces, and frontend rendering/prefill behavior are covered by focused tests.

## Non-goals

- Do not allow workstream agents to submit side-effecting commands on behalf of users.
- Do not implement direct agent tool access to `WorkstreamService.runAction(...)` for mutations.
- Do not weaken backend authorization, selected `AuthContext` checks, idempotency, audit traces, provider fail-closed behavior, or frontend secret boundaries.
- Do not create a new workstream or replace the existing surface/action architecture.
- Do not modify `skills-pack/**` for this app-realization mini-project.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run required checks or mark blocked with a precise reason, and make one focused git commit before being marked `done`.

## Read order for future task sessions

1. `AGENTS.md`
2. `specs/AGENTS.md`
3. `specs/workstream-surface-intent-routing/README.md`
4. `specs/workstream-surface-intent-routing/conversation-capture.md`
5. `specs/workstream-surface-intent-routing/pending-tasks.md`
6. selected sprint/backlog/task brief
7. task-specific backend/frontend/app-description/test files

## Sprint sequence

1. Sprint 01: Router contract and User Admin proof.
2. Sprint 02: Frontend prefill and no-model routing tests.
3. Sprint 03: Surface catalogs and all-workstream expansion.
4. Sprint 04: Agent familiarity seed updates and terminal verification.

## Open concerns

- Whether surface catalog metadata should be code-owned in `WorkstreamService`, app-description-owned, or both. Initial tasks should choose the smallest stable implementation and document any follow-up.
- How much natural-language parsing should remain deterministic versus falling back to the model. Initial routing should prefer high-confidence patterns and safe ambiguity handling.
- Whether future direct agent command submission warrants a separate mini-project after surface routing proves useful.
