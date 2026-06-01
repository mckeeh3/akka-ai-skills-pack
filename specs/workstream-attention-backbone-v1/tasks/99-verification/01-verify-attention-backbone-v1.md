# TASK-WAB-99-001: Verify attention backbone v1 completion

## Objective

Verify that the v1 attention backbone mini-project reached its stated done state, or append new bounded follow-up tasks plus a new terminal verification task.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-attention-backbone-v1/README.md`
- `specs/workstream-attention-backbone-v1/conversation-capture.md`
- `specs/workstream-attention-backbone-v1/pending-tasks.md`
- `specs/workstream-attention-backbone-v1/sprints/01-attention-backbone-v1-sprint.md`
- `specs/workstream-attention-backbone-v1/backlog/01-attention-backbone-v1-build-backlog.md`
- all task briefs under `specs/workstream-attention-backbone-v1/tasks/`
- implementation artifacts changed by earlier tasks

## Skills

- none; repository verification task

## In scope

- Compare completed work against mini-project done state.
- Run targeted searches and tests sufficient to validate v1 claims.
- Confirm My Account, workstream dashboards, and left rail use backend-derived attention at v1 scope.
- Confirm authorization, redaction, lifecycle, audit/trace, and tests are represented.
- Append bounded follow-up tasks before a new terminal verification task if material gaps remain.
- Record completion notes in `pending-tasks.md` if no material gaps remain.

## Out of scope

- Whole-repository architecture review.
- Expanding scope to full event/autonomous-agent notification platform unless a gap directly blocks v1 done state.

## Expected outputs

- Updated `pending-tasks.md` with verification notes and status.
- Optional verification summary or appended follow-up task briefs if gaps remain.

## Required checks

- `git diff --check`
- targeted backend tests for attention/My Account/workstream services
- targeted frontend tests for workstream/rail/My Account attention rendering
- `rg -n "AttentionItem|attention summary|list_personal_attention|open_attention_item|railAttentionState|personalAttention" templates/ai-first-saas-starter specs/workstream-attention-backbone-v1`

## Done criteria

- Sprint goals and mini-project done state are explicitly assessed.
- Any gaps are either proven non-blocking, documented as recommendations, or converted into new bounded pending tasks followed by a new terminal verification task.
- If complete, queue records completion with no new required work.
- Task changes and queue update are committed.

## Commit message

`attention-backbone: verify v1 completion`
