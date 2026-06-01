# TASK-WAB-01-001: Define starter attention backbone v1 contract

## Objective

Define the v1 shared attention backbone contract for the starter template so backend and frontend implementation tasks have a precise target.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-attention-backbone-v1/README.md`
- `specs/workstream-attention-backbone-v1/conversation-capture.md`
- `specs/workstream-attention-backbone-v1/sprints/01-attention-backbone-v1-sprint.md`
- `specs/workstream-attention-backbone-v1/backlog/01-attention-backbone-v1-build-backlog.md`
- `specs/workstream-attention-backbone-v1/tasks/01-contracts/01-define-attention-contract.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/capability-first-backend-architecture.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/workstream-dashboard-attention-event-backbone-wip.md`
- existing starter backend/frontend attention-related files found with `rg "attention|Attention|open_attention_item|list_personal_attention" templates/ai-first-saas-starter`

## Skills

- none required; repository/starter planning and contract task

## In scope

- Add or update a focused starter/reference contract document or test fixture describing:
  - `AttentionItem` fields and lifecycle states;
  - summary shape for left rail and My Account;
  - workstream-local detail reads;
  - capability ids/governed-tool ids for read and lifecycle operations;
  - tenant/customer/AuthContext filtering and redaction rules;
  - source refs for traces, workflows, autonomous tasks, surfaces/actions, and capabilities;
  - distinction between actionable backend attention and frontend unseen-response state.
- Update queue notes if this task discovers a necessary split.

## Out of scope

- Implementing Java or TypeScript runtime behavior.
- Introducing advanced autonomous task notifications or realtime streams beyond contract placeholders.

## Expected outputs

- A focused contract artifact under `specs/workstream-attention-backbone-v1/` or starter docs/tests.
- Updated `pending-tasks.md` status/notes.

## Required checks

- `git diff --check`
- focused `rg` showing the new contract names the shared backbone and guards against frontend-only authority.

## Done criteria

- Later backend/frontend tasks can implement without guessing attention fields, lifecycle, projections, capabilities, or redaction rules.
- Contract preserves one shared backbone with workstream-scoped ownership/projections.
- Task changes and queue update are committed.

## Commit message

`attention-backbone: define v1 contract`
