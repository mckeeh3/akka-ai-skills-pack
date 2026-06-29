# App-description Implementation Alignment Mini-project

## Purpose

Align the refreshed root `app-description/` with the real runnable SaaS Foundation App implementation, tests, and runtime-validation evidence.

The previous `specs/app-description-refresh/` mini-project closed description refresh only. It intentionally left all five foundation workstreams as `stale-description-changed`. This mini-project turns that state into focused build/compile and runtime-validation work without guessing from stale implementation or claiming runtime readiness from description-only artifacts.

## Target scope

Primary app-facing assets:

- `app-description/domains/core-starter/workstreams/**/lifecycle.md`
- `app-description/domains/core-starter/workstreams/**/realization/source-alignment.md`
- `specs/app-description-implementation-alignment/**`
- `specs/runtime-validation/**` when scenario corpus or run-record scaffolding is authored

Read-only or implementation evidence inputs:

- `src/main/java/ai/first/**`
- `src/test/java/ai/first/**`
- `frontend/**`
- `src/main/resources/**`
- existing active specs and docs

Runtime source/frontend changes are allowed only in later tasks if a selected task explicitly scopes one bounded workstream alignment fix and requires the relevant checks. The initial tasks are audit/scenario/task-authoring work.

## Relationship to refreshed app-description

Source of truth:

- `app-description/**` is current intent.
- `specs/app-description-refresh/terminal-verification.md` is the completed description-refresh proof.
- Existing source/frontend/tests are implementation evidence, not product authority when they conflict with refreshed current intent.

Default rule: if implementation differs from refreshed app-description, classify the mismatch as one of:

- already aligned;
- implementation gap;
- test gap;
- runtime-validation gap;
- source-alignment evidence gap;
- app-description overreach requiring description correction;
- provider/config blocker;
- auth/setup/seed blocker.

Do not silently treat source behavior as the new intent and do not claim `runtime-ready` without real local Akka/API/UI/provider/fail-closed evidence.

## Workstream order

1. My Account
2. User Admin
3. Agent Admin
4. Governance/Policy
5. Audit/Trace

This order starts with authentication/account context, then admin foundation, then managed-agent governance, then policy, then investigation/audit surfaces.

## Done state

This mini-project is complete when:

- current implementation/source evidence is inventoried against refreshed app-description source-alignment expectations;
- a runtime-validation corpus skeleton exists for the five foundation workstreams or explicit scenario gaps are recorded;
- each foundation workstream has an alignment result that updates lifecycle/source-alignment evidence or queues exact remediation/runtime-validation work;
- the build/compile/runtime-validation follow-up queue is consolidated and executable one task per fresh context;
- terminal verification records which workstreams are aligned, partially aligned, blocked, or still stale, without overclaiming runtime readiness.

## Non-goals

- Do not redo the app-description refresh.
- Do not edit `skills-pack/**`.
- Do not edit installed `.agents/skills/**` as project source.
- Do not run multiple queued workstream tasks in parallel.
- Do not count mocked/demo/fixture-only behavior as runtime-ready.

## Execution model

Execute one queued task per fresh harness context. Each task must:

1. mark exactly one task `in-progress` before edits;
2. execute only that task;
3. run required checks or block with a precise reason;
4. mark `done` only when checks and done criteria pass;
5. commit the task changes and queue update together;
6. report the next runnable task.

Parent orchestration should use `pi-subagents` sequentially: one fresh-context worker subagent per task, no parallel queue execution.
