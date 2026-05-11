# TASK-08-004: Implement supplies views, trace fanout, and stale-decision timer

## Purpose

Add supervision read models and scheduled stale-decision behavior for the supplies autopilot reference slice.

## Required reads

- `AGENTS.md`
- `docs/ai-first-saas-application-architecture.md`
- `specs/ai-first-skills-pack-migration/sprints/08-executable-ai-first-reference-slice-sprint.md`
- `specs/ai-first-skills-pack-migration/backlog/08-executable-ai-first-reference-slice-build-backlog.md`
- supplies domain/entity/workflow files from `TASK-08-001` through `TASK-08-003`

## Scope

- Add views for supply risk rows, pending supply decisions, auto-ship/suppression history, and trace lookup.
- Add a stale-decision timed action that safely calls back into the workflow or entity.
- Add `SupplyTraceConsumer` only if needed to enrich/project trace facts cleanly.
- Add projection, timer, duplicate/replayed event, and trace visibility tests.

## Non-goals

- No frontend implementation.
- No external event publication unless required for local trace projection.

## Skills

- `ai-first-saas`
- `ai-first-saas-audit-trace`
- `akka-views`
- `akka-view-from-event-sourced-entity`
- `akka-view-from-workflow`
- `akka-view-query-patterns`
- `akka-view-testing`
- `akka-timed-actions`
- `akka-timers-scheduling`
- `akka-timed-action-component`
- `akka-timed-action-testing`
- `akka-consumers`
- `akka-consumer-from-event-sourced-entity`
- `akka-consumer-testing`

## Expected outputs

- Supplies view classes and optional trace consumer.
- Stale-decision timed action.
- Focused view/timer/consumer tests.

## Required checks

- Run focused view, timer, and consumer tests.
- Verify View queries obey Akka query constraints, especially `ORDER BY`/`WHERE` rules.

## Done criteria

- Supervision queues and trace lookup are backed by durable facts.
- Timer callbacks are idempotent and do not duplicate side effects.