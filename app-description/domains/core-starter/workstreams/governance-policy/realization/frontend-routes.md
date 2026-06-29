# Realization: Frontend routes and surfaces for Governance/Policy

Capability: `governance-policy-lifecycle`.

This map is docs-only candidate evidence. It describes browser realization expected from the refreshed workstream graph and does not claim runtime alignment.

## Frontend evidence

| Surface / route concern | Candidate frontend evidence | Notes |
|---|---|---|
| Governance/Policy dashboard | `frontend/src/workstream/surfaces/DashboardSurface.tsx` | Shows actionable pending approvals, simulation findings, exceptions, rollback candidates, denials, partial failures, recent changes, and shortcuts. |
| Policy catalog | `ListSearchSurface.tsx` or successor typed collection surface | Search/filter policies by category, lifecycle state, workstream, agent, tool/action, role, scope, approval state, and exception state. |
| Policy detail/effective decision | detail/show inspection surface components | Renders active version, drafts, effective decision, exceptions, scope precedence, decision evidence, rollback targets, trace links, and authorized actions. |
| Policy draft/edit | typed create/edit surface components and `SurfaceActionBar.tsx` | Supports proposal fields, reason, affected workstreams/tools/roles, idempotency, validation, no-active-mutation result, and next simulation/approval action. |
| Simulation findings | evidence/result panels | Renders expected changed decisions, risk/impact/confidence, evidence gaps, partial-failure state, and required approval gates. |
| Decision-card review | approval/decision-card components | Supports approve, reject, request evidence, modify/counterpropose, defer, escalate, exception-required, and activate-approved-version actions with reviewer authority. |
| Exception review | exception lifecycle surface | Supports grant, deny, revoke, expire, request evidence, expiry display, runtime effect, and trace links. |
| Rollback and result surfaces | result/partial-failure card components | Distinguishes committed, not-committed, partial-publication, idempotent replay, stale/conflict, denied, and failed states for activation/rollback/exception actions. |
| Policy history and runtime outcome links | timeline/history, `AuditTimelineSurface.tsx`, `TraceLinkList.tsx` | Shows drafts, simulations, decisions, activations, exceptions, rollback, denials, and runtime policy-decision traces without protected-data leakage. |
| Typed browser API client | `frontend/src/api/HttpWorkstreamApiClient.ts`, `WorkstreamApiClient.ts`, `types.ts` | DTOs and errors must preserve lifecycle state, decision-card payloads, idempotency, denials, validation states, result/partial-failure/system-message surfaces, and trace refs. |

## Route/deep-link expectations

- Workstream route: Governance/Policy rail item opens `surface-governance-policy-dashboard` for authorized roles.
- Deep links may target catalog filters, policy detail, draft, simulation result, decision card, exception, rollback result, or history when backend authorization allows.
- Direct/deep-link denials render safe `system_message` surfaces without hidden target enumeration.
- Composer surface-intent routes may open or prefill catalog/detail/draft/simulation/decision/exception/history surfaces, but deterministic routing never submits, approves, activates, rolls back, or grants exceptions.
- Confirmed chat-plan UI uses plan proposal, exact confirmation, cancellation, approval-required, result, partial-failure, and denial surfaces for already-modeled governed tools.

## Validation evidence

Future source-alignment/runtime-validation should map or create tests for:

- `frontend/src/workstream-governance-policy-vertical.contract.test.mjs`
- `frontend/src/governance-audit-admin-profile.contract.test.mjs`
- `frontend/src/workstream-surfaces.contract.test.mjs`
- `frontend/src/workstream-actions.contract.test.mjs`
- accessibility/responsive tests for dashboard, catalog, detail, draft, simulation, decision card, exception, result, partial-failure, history, and `system_message` states

## Gaps / caveats

- Governance/Policy frontend realization must stay in the agent workstream shell (`frontend/src/workstream/**`); removed page/screen modules are not reference or fallback architecture.
- Existing UI tests/surfaces may still reflect older settings-only or proposal/diff/simulation intent and must be reconciled before claiming current alignment.
- Browser payloads must not expose raw implementation ids as primary copy, provider/server secrets, raw prompts/model/tool payloads, hidden authority state, or raw idempotency/correlation internals.
