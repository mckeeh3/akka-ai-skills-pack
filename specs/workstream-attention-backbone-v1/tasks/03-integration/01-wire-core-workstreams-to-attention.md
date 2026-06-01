# TASK-WAB-03-001: Wire core workstreams to shared attention

## Objective

Replace hard-coded and dashboard-local starter attention with shared backend attention reads/producers for the five core workstream baseline where v1 scope applies.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-attention-backbone-v1/README.md`
- `specs/workstream-attention-backbone-v1/conversation-capture.md`
- `specs/workstream-attention-backbone-v1/sprints/01-attention-backbone-v1-sprint.md`
- `specs/workstream-attention-backbone-v1/backlog/01-attention-backbone-v1-build-backlog.md`
- `specs/workstream-attention-backbone-v1/tasks/03-integration/01-wire-core-workstreams-to-attention.md`
- contract artifact from `TASK-WAB-01-001`
- backend attention files/tests from `TASK-WAB-02-001`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/MyAccountService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java`
- relevant User Admin, Agent Admin, Audit/Trace, Governance/Policy services

## Skills

- none required unless implementation discovers a focused Akka component gap

## In scope

- Change `MyAccountService.personalAttention` to read authorized items from the shared attention service instead of hard-coded capability-derived records.
- Update workstream dashboard surface builders to pull workstream-local attention from the shared attention service where v1 producers exist.
- Add initial producer/derivation paths for existing starter states:
  - User Admin invitation delivery failure/expiry-like attention;
  - Agent Admin provider readiness blocked attention;
  - Governance policy proposal/approval attention;
  - Audit/Trace provider failure/evidence attention.
- Wire `open_attention_item` to validate target item/surface/workstream authorization and return safe denial/redaction where appropriate.
- Update backend tests for My Account aggregation and workstream dashboard attention.

## Out of scope

- Frontend rail rendering changes except adjusting contract fixtures if unavoidable.
- Realtime streams, digests, timers, and AutonomousAgent task notification ingestion.

## Expected outputs

- Updated backend services using the shared attention service.
- Updated backend tests.
- Updated queue status/notes.

## Required checks

- `git diff --check`
- targeted backend tests for My Account, WorkstreamService, and attention service
- focused `rg` proving old hard-coded personal attention item construction has been removed or replaced by shared service calls

## Done criteria

- My Account aggregate attention is backend-derived from shared attention state.
- Workstream dashboards use shared attention for v1 items without losing workstream-local semantics.
- Hidden workstreams/items are redacted and audited.
- Task changes and queue update are committed.

## Commit message

`attention-backbone: wire core workstreams`
