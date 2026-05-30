# Task: Implement frontend Audit/Trace runtime-aligned surfaces

## Objective

Align the frontend Audit/Trace structured surfaces, action handling, fixtures, and contract tests with backend runtime DTOs and cross-workstream trace-link behavior.

## Required reads

- AGENTS.md
- specs/full-core-smb-audit-trace/README.md
- specs/full-core-smb-audit-trace/audit-trace-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- docs/web-ui-style-guide.md

## In scope

- Update Audit/Trace surface types, fixtures, renderers, and action handling for backend DTOs.
- Render dashboard health cards, search/filter rows, detail/evidence, correlation timelines, failure evidence, investigation guidance, partial/redacted/non-enumerating states, and trace links.
- Preserve backend-authoritative denial language; frontend hidden/disabled controls are advisory only.
- Keep provider/tool/model/worker failure evidence browser-safe and visually distinct.
- Update focused frontend contract tests.

## Out of scope

- Do not implement backend service logic.
- Do not implement model-backed AuditTraceAgent behavior.
- Do not implement a worker lifecycle.

## Expected outputs

- Frontend source/test changes under `templates/ai-first-saas-starter/frontend/src/`.
- Root `frontend/` synchronization only if this repo's mirror convention requires it for touched files.
- Updated queue status.

## Required checks

```bash
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-audit-trace-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/api.contract.test.mjs
rg -n "audit\.trace|Audit/Trace|Audit correlation timeline|redacted|Partial results|provider|tool|model|worker|system_message|Frontend affordances never grant authority|traceLinks" templates/ai-first-saas-starter/frontend/src --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- Audit/Trace frontend surfaces are runtime-aligned, polished, accessible, trace-linked, and safe.
- Cross-workstream trace links route to Audit/Trace actions/surfaces without implying frontend authority.
- No secrets, hidden prompts, or cross-tenant evidence are exposed in browser-visible fixture/runtime shapes.

## Commit message

- `full-core-smb: implement audit trace frontend surfaces`
