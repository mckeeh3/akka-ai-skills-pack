# Task: Implement My Account attention, trace refs, and frontend surfaces

## Objective

Implement authorized personal attention aggregation, own trace refs, and runtime-aligned frontend My Account surfaces/fixtures/actions.

## Required reads

- AGENTS.md
- specs/full-core-smb-my-account/README.md
- specs/full-core-smb-my-account/conversation-capture.md
- specs/full-core-smb-my-account/my-account-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- specs/my-account-workstream-v0/workstream-contract.md
- predecessor task changes from TASK-FCSMB-MA-01-002
- templates/ai-first-saas-starter/frontend/src/workstream/fixtures/me.ts
- templates/ai-first-saas-starter/frontend/src/workstream/fixtures/agents.ts
- templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts
- templates/ai-first-saas-starter/frontend/src/workstream/types/auth.ts
- templates/ai-first-saas-starter/frontend/src/workstream/types/surfaces.ts
- templates/ai-first-saas-starter/frontend/src/workstream/rail/FunctionalAgentRail.tsx
- templates/ai-first-saas-starter/frontend/src/workstream/shell/WorkstreamShell.tsx
- templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DashboardSurface.tsx
- templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DetailEditSurface.tsx

## In scope

- Add backend personal attention and own trace-ref DTOs/actions if not completed by the previous backend task.
- Aggregate only authorized sibling-workstream attention items; avoid hidden-workstream names/count leakage.
- Route trace/detail links through authorized Audit/Trace behavior and safe redaction metadata.
- Update frontend fixtures/types/renderers/contracts for My Account dashboard/profile/settings/context/attention/trace states.
- Prove lower-left user tile launch and no duplicate top-rail My Account launcher remain intact.

## Out of scope

- Do not implement MyAccountAgent evidence tools or digest workers.
- Do not add top-rail My Account navigation.
- Do not expose hidden workstream names, provider secrets, hidden prompts, raw JWTs, invitation tokens, or cross-tenant data.

## Expected outputs

- Updated backend/frontend source and tests for My Account attention/trace/surface behavior.
- New or updated `workstream-my-account-vertical.contract.test.mjs` if needed.
- Updated queue status.

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=MeServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-my-account-vertical.contract.test.mjs src/workstream-shell.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/api.contract.test.mjs
rg -n "My Account|my_account\.list_personal_attention|personal attention|trace refs|view_own_trace_refs|open_authorized_workstream|user tile|agent-my-account|surface-my-account-dashboard|system_message|not_found_or_redacted|blocked_provider_or_runtime|no duplicate top-rail" templates/ai-first-saas-starter --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- My Account aggregates only authorized personal attention and trace refs.
- Frontend renders My Account states with accessible, responsive, workstream-first surfaces.
- Lower-left user tile launch is preserved and no top-rail duplicate appears.
- Task changes and queue update are committed with `full-core-smb: implement my account attention surfaces`.
