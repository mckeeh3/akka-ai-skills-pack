# Task: Align frontend Governance/Policy surfaces and actions

## Objective

Align Governance/Policy frontend fixtures, action ids, surface DTO assumptions, and contract tests with backend runtime surfaces and deterministic lifecycle semantics.

## Required reads

- AGENTS.md
- specs/full-core-smb-governance-policy/README.md
- specs/full-core-smb-governance-policy/governance-policy-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- task outputs from `TASK-FCSMB-GP-01-002` and `TASK-FCSMB-GP-01-003`
- templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts
- templates/ai-first-saas-starter/frontend/src/api/FixtureWorkstreamApiClient.ts
- templates/ai-first-saas-starter/frontend/src/api/HttpWorkstreamApiClient.ts
- templates/ai-first-saas-starter/frontend/src/workstream-governance-policy-vertical.contract.test.mjs

## In scope

- Align `action-govpol-*` fixture ids with backend `action-governance-policy-*` ids or document/test explicit aliases.
- Render dashboard, inventory, policy detail, proposal, simulation, decision, activation-blocked, rollback-blocked, and impact-analysis blocked states from runtime-shaped DTOs.
- Preserve backend-authoritative denial copy, idempotency/no-op status, trace links, redaction markers, provider/runtime blocked states, and accessibility labels.
- Update frontend contract tests for action ids, capability ids, surface ids, state coverage, and no fake analysis/progress.
- Synchronize root `frontend/` only if touched starter UI files have mirrored root copies by repository convention.

## Out of scope

- Do not implement backend lifecycle changes.
- Do not implement model-backed GovernancePolicyAgent changes.
- Do not fabricate successful policy-impact worker output.

## Expected outputs

- updated frontend workstream fixtures/types/renderers/action client files as needed
- updated `workstream-governance-policy-vertical.contract.test.mjs` and related contract tests

## Required checks

```bash
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-governance-policy-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/api.contract.test.mjs
rg -n "action-governance-policy|action-govpol|governance\.policy|Governance/Policy|policy inventory|proposal|simulation|decision|activate|rollback|blocked_provider_or_runtime|system_message|trace|no fake|no direct mutation" templates/ai-first-saas-starter/frontend/src --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- Frontend and backend action/surface contracts no longer diverge silently.
- Governance/Policy UI is structured, accessible, trace-linked, redacted, and explicit that backend checks are authoritative.
- Impact analysis remains visibly blocked/provider-runtime unless a real worker path exists.

## Commit message

- `full-core-smb: align governance policy frontend surfaces`
