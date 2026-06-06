# TASK-WCC-01-002: Add mapping, runtime evidence, and internal worker manifest contracts

## Objective

Extend the workstream manifest docs/schema/validator with lightweight implementation-readiness structures for surface action/governed-tool mappings, runtime evidence, and structured internal workers.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/workstream-contract-cleanup/README.md`
- `specs/workstream-contract-cleanup/conversation-capture.md`
- `specs/workstream-contract-cleanup/sprints/01-workstream-contract-cleanup-sprint.md`
- `specs/workstream-contract-cleanup/backlog/01-workstream-contract-cleanup-build-backlog.md`
- `specs/workstream-contract-cleanup/tasks/02-runtime-evidence/01-add-mapping-evidence-internal-worker-contracts.md`
- `skills-pack/docs/workstream-contract.md`
- `skills-pack/docs/workstream-manifest-schema.md`
- `skills-pack/docs/workstream-manifest.schema.json`
- `skills-pack/docs/structured-surface-contracts.md`
- `skills-pack/tools/validate-workstream-manifest.py`
- `skills-pack/templates/ai-first-saas-core-app/app-description/12-workstreams/workstream-manifest.json`

## Skills

- none; skills-pack docs/tooling maintenance task

## In scope

- Add optional lightweight `surfaceActionMappings` or `governedToolMappings` structure.
- Require that mapping structure when readiness is `capability-ready`, `expertise-ready`, `runtime-ready`, or `production-ready`.
- Include fields sufficient for referential integrity: `surfaceId`, `actionId`, `capabilityId`, `governedToolId`, exposure channel, auth basis or role/capability summary, idempotency summary, result/system-message surface, and trace requirement.
- Add explicit readiness evidence fields required for `runtime-ready` and `production-ready`, such as local commands, API/UI smoke path, provider/security fail-closed check, and trace evidence.
- Replace string-only `internalWorkers` with structured entries when present, while allowing omitted/empty workers when no internal/background work is claimed.
- Update the foundation template to either provide the new structures or lower/defer readiness claims so validation remains honest.

## Out of scope

- Deep full-surface schema modeling that duplicates `structured-surface-contracts.md`.
- Runtime app implementation.
- Installed-layout reference validation.

## Expected outputs

- Updated manifest docs/schema/validator/template.
- Queue status update in `specs/workstream-contract-cleanup/pending-tasks.md`.

## Required checks

```bash
git diff --check
python3 skills-pack/tools/validate-workstream-manifest.py skills-pack/templates/ai-first-saas-core-app/app-description
bash skills-pack/tools/validate-workstream-contracts.sh skills-pack/templates/ai-first-saas-core-app/app-description
```

## Done criteria

- Accepted decisions 5, 6, 10, 11, and 12 are reflected in docs/schema/validator/template.
- Conditional validation for readiness behaves as documented.
- Foundation template validates or is explicitly adjusted to avoid over-claiming readiness.
- The queue is updated and the task changes are committed.

## Commit message

```text
skills-pack: add workstream readiness mapping contracts
```
