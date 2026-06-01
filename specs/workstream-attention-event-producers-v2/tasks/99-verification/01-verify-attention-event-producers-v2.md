# TASK-WAEP-99-001: Verify attention event producers v2 completion

## Objective

Verify that the v2 attention producer mini-project reached its stated done state, or append new bounded follow-up tasks plus a new terminal verification task.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-attention-event-producers-v2/README.md`
- `specs/workstream-attention-event-producers-v2/conversation-capture.md`
- `specs/workstream-attention-event-producers-v2/pending-tasks.md`
- `specs/workstream-attention-event-producers-v2/sprints/01-event-producers-v2-sprint.md`
- `specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md`
- all task briefs under `specs/workstream-attention-event-producers-v2/tasks/`
- v1 attention contract and implementation files
- implementation artifacts changed by earlier v2 tasks

## Skills

- none; repository verification task

## In scope

- Compare completed work against mini-project done state.
- Run targeted searches and tests sufficient to validate v2 claims.
- Confirm attention is produced/updated/resolved from real backend events or state transitions at v2 scope.
- Confirm timed/stale and worker/task states are represented honestly.
- Confirm update delivery uses backend-derived summaries and frontend-only state is not authoritative.
- Confirm docs/guidance no longer state or imply that v1 attention backbone is missing.
- Append bounded follow-up tasks before a new terminal verification task if material gaps remain.

## Out of scope

- Whole-repository architecture review.
- Full enterprise event/notification platform validation beyond v2 done state.

## Expected outputs

- Updated `pending-tasks.md` with verification notes and status.
- Optional completion summary or appended follow-up task briefs if gaps remain.

## Required checks

- `git diff --check`
- targeted backend tests for attention producers/timed/worker behavior
- targeted frontend tests/typecheck/build for attention update delivery
- `rg -n "AttentionProducer|attention producer|upsertAttention|resolveAttention|timed attention|blocked_provider_or_runtime|railAttentionState|attention backbone" templates/ai-first-saas-starter docs specs/workstream-attention-event-producers-v2`

## Done criteria

- Sprint goals and mini-project done state are explicitly assessed.
- Any gaps are either proven non-blocking, documented as recommendations, or converted into new bounded pending tasks followed by a new terminal verification task.
- If complete, queue records completion with no new required work.
- Task changes and queue update are committed.

## Commit message

`attention-producers: verify v2 completion`
