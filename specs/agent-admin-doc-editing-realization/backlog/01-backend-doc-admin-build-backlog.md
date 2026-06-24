# Backlog 01: Backend doc administration

## Design notes

The backend should adapt or replace existing governed prompt/skill/reference document code so current Agent Admin behavior is versioned doc editing rather than broad behavior governance. Existing entities under `src/main/java/ai/first/application/foundation/agent/` and domain records under `src/main/java/ai/first/domain/foundation/agent/` are likely useful substrate, but old behavior proposal, prompt-risk review, model ref, tool-boundary, seed import, and activation semantics must not drive the new Agent Admin user-facing contract.

## Implementation areas

- `src/main/java/ai/first/domain/foundation/agent/**`
- `src/main/java/ai/first/application/foundation/agent/**`
- `src/main/java/ai/first/application/coreapp/agentadmin/**`
- `src/main/java/ai/first/api/coreapp/admin/**`
- `src/test/java/ai/first/application/foundation/agent/**`
- `src/test/java/ai/first/application/coreapp/agentadmin/**`

## Task breakdown

### AADE-01-001 — Backend contract and service slice

Define or revise service-layer records and methods for:

- list/filter all agents by name and workstream/domain;
- read/update agent name/purpose;
- read agent detail with prompt, skills, reference docs, last edit time, and trace links;
- read current/historical prompt, skill, and reference doc versions;
- start/revise/cancel/save edit session contract boundaries;
- read version history and selected version detail;
- adjacent-version diff;
- restore historical version;
- SaaS-admin-only authorization checks.

### AADE-01-002 — Durable doc/version lifecycle

Implement durable behavior for:

- simple integer versions;
- current/latest version tracking;
- version metadata: created time, actor, content, edit-session transcript/summary;
- historical read-only behavior;
- restore creating a new current version;
- skill create/update/delete;
- reference doc create/update/delete;
- skill deletion cascading permanent reference doc deletion;
- stale current-version save rejection/recovery.

## Validation

Use targeted Maven tests first, then broader `mvn test` when feasible. At minimum, backend tasks should add or update tests proving the new semantics before frontend tasks rely on them.
