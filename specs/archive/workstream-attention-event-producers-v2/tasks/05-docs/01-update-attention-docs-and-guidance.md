# TASK-WAEP-05-001: Update attention docs and guidance

## Objective

Update local starter/reference docs and relevant guidance so future agents understand that v1 attention backbone exists and v2 producer/update behavior is the next layer.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-attention-event-producers-v2/README.md`
- `specs/workstream-attention-event-producers-v2/conversation-capture.md`
- `specs/workstream-attention-event-producers-v2/sprints/01-event-producers-v2-sprint.md`
- `specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md`
- `specs/workstream-attention-event-producers-v2/tasks/05-docs/01-update-attention-docs-and-guidance.md`
- `specs/workstream-attention-backbone-v1/README.md`
- v2 contract and completed implementation notes
- relevant docs found with `rg -n "attention backbone|attention queue|needs my attention|WorkstreamAttention|frontend-only badge|workstream-dashboard-attention" docs templates specs/workstream-attention-backbone-v1`

## Skills

- none; docs/guidance task

## In scope

- Update starter docs and local guidance to distinguish:
  - v1 shared attention backbone;
  - v2 event/service/timer/task producers;
  - future full event/message backbone and digests.
- Update or annotate WIP/provenance docs that still imply the attention backbone is only conceptual.
- Add references to validation expectations and runtime completion guardrails.

## Out of scope

- Registering project-only docs in `pack/manifest.yaml` unless the edited doc is already packaged and appropriate.
- Broad rewrite of AI-first SaaS doctrine unrelated to attention.

## Expected outputs

- Focused docs/guidance updates.
- Updated queue status/notes.

## Required checks

- `git diff --check`
- focused `rg` showing docs distinguish implemented v1, v2 producers, and future work without stale “not implemented” claims

## Done criteria

- Future agents will not incorrectly report the attention backbone as missing after v1/v2 work.
- Docs preserve backend-authoritative attention and runtime completion standards.
- Task changes and queue update are committed.

## Commit message

`attention-producers: update docs guidance`
