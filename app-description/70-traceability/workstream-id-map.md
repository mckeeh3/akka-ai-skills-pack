# Workstream ID Map

This map is the canonical design-to-implementation alias contract for the five core functional-agent workstreams. It does not rename runtime ids; implementation ids remain stable until a dedicated runtime migration updates backend, frontend, and tests together.

## ID conventions

- Canonical app-description functional-agent ids use the `*-agent` suffix, for example `my-account-agent`.
- Runtime/browser functional-agent ids use the `agent-*` prefix returned by `/api/me` and `WorkstreamService`, for example `agent-my-account`.
- Runtime workstream icon ids use the `workstream-*` prefix in `WorkstreamIconDescriptor`.
- Canonical app-description primary dashboard surface ids may omit the runtime `surface-` prefix; runtime/API/frontend surface ids include it.
- Deep links and shell requests use runtime ids: `functionalAgentId=<agent-id>` and `surfaceId=<surface-id>`.

## Core functional-agent and workstream aliases

| Canonical app-description functional agent | Runtime/API/frontend functional agent id | Runtime workstream icon id | Display label | Primary capability family | Visibility/source locations |
| --- | --- | --- | --- | --- | --- |
| `my-account-agent` | `agent-my-account` | `workstream-my-account` | My Account | `secure-tenant-user-foundation`, `frontend-shell-integration-patterns` | `/api/me` `functionalAgents[]`; signed-in user tile, not duplicated in top rail; `MeResponse.FunctionalAgentSummary`; `frontend/src/workstream/types/agents.ts` |
| `user-admin-agent` | `agent-user-admin` | `workstream-user-admin` | User Admin | `secure-tenant-user-foundation`, `governance-decisions-audit` | `/api/me` `functionalAgents[]`; left rail when visible; `MeResponse.FunctionalAgentSummary`; `WorkstreamService` |
| `agent-admin-agent` | `agent-agent-admin` | `workstream-agent-admin` | Agent Admin | `managed-agent-foundation`, `governance-decisions-audit` | `/api/me` `functionalAgents[]`; left rail when visible; `MeResponse.FunctionalAgentSummary`; `WorkstreamService` |
| `audit-trace-agent` | `agent-audit-trace` | `workstream-audit-trace` | Audit/Trace | `governance-decisions-audit`, `managed-agent-foundation` | `/api/me` `functionalAgents[]`; left rail when visible; `MeResponse.FunctionalAgentSummary`; `WorkstreamService` |
| `governance-policy-agent` | `agent-governance-policy` | `workstream-governance-policy` | Governance/Policy | `governance-decisions-audit`, `managed-agent-foundation` | `/api/me` `functionalAgents[]`; left rail when visible; `MeResponse.FunctionalAgentSummary`; `WorkstreamService` |

## Primary dashboard surface aliases

| Canonical app-description default surface | Runtime/API/frontend primary surface id | Runtime owner functional agent id | Frontend dashboard resolver | Backend resolver |
| --- | --- | --- | --- | --- |
| `my-account-dashboard` | `surface-my-account-dashboard` | `agent-my-account` | `dashboardSurfaceIdForAgent('agent-my-account')` | `dynamicSurface(..., 'surface-my-account-dashboard', ...)` |
| `user-admin-dashboard` | `surface-user-admin-dashboard` | `agent-user-admin` | default branch of `dashboardSurfaceIdForAgent(...)` for `agent-user-admin` | `dynamicSurface(..., 'surface-user-admin-dashboard', ...)` |
| `agent-governance-center` as Agent Admin dashboard | `surface-agent-admin-catalog` | `agent-agent-admin` | `dashboardSurfaceIdForAgent('agent-agent-admin')` | `dynamicSurface(..., 'surface-agent-admin-catalog', ...)` |
| `audit-trace-explorer` as Audit/Trace dashboard | `surface-audit-trace-dashboard` | `agent-audit-trace` | `dashboardSurfaceIdForAgent('agent-audit-trace')` | `dynamicSurface(..., 'surface-audit-trace-dashboard', ...)` |
| `agent-governance-center` / governance dashboard | `surface-governance-policy-dashboard` | `agent-governance-policy` | `dashboardSurfaceIdForAgent('agent-governance-policy')` | `dynamicSurface(..., 'surface-governance-policy-dashboard', ...)` |

## Additional implemented core surface ids

These runtime ids are already resolvable through `WorkstreamService.dynamicSurface(...)` and should be referenced by later surface/action mapping tasks rather than guessed.

| Functional agent runtime id | Implemented runtime surface ids |
| --- | --- |
| `agent-my-account` | `surface-my-account-dashboard`, `surface-my-account-notification-center`, `surface-my-account-personal-attention-digest-progress`, `surface-my-account-personal-attention-digest-result`, `surface-my-account-personal-attention-digest-blocked` |
| `agent-user-admin` | `surface-user-admin-dashboard`, `surface-user-admin-list`, `surface-user-admin-invitation-panel`, `surface-user-admin-detail-admin`, `surface-user-admin-access-review`, `surface-user-admin-role-change-preview` |
| `agent-agent-admin` | `surface-agent-admin-catalog`, `surface-agent-admin-detail`, `surface-agent-admin-trace` |
| `agent-audit-trace` | `surface-audit-trace-dashboard`, `surface-audit-trace-search`, `surface-audit-trace-detail`, `surface-audit-trace-timeline`, `surface-audit-timeline`, `surface-audit-trace-failure-evidence`, `surface-audit-trace-investigation-guide`, `surface-audit-trace-investigation-note`, `surface-audit-trace-summary-task`, `surface-audit-trace-summary-progress` |
| `agent-governance-policy` | `surface-governance-policy-dashboard`, `surface-governance-policy-inventory`, `surface-governance-policy-detail`, `surface-governance-policy-proposal`, `surface-governance-policy-simulation`, `surface-governance-policy-decision`, `surface-governance-policy-activation-blocked`, `surface-governance-policy-rollback-blocked`, `surface-governance-policy-impact-analysis-task` |

## Shell request and deep-link contract

- Frontend selection and deep links use runtime ids in `WorkstreamSelection`: `selectedFunctionalAgentId`, `selectedSurfaceId`, and `surfacePlacement`.
- Composer dashboard shortcuts and dashboard buttons create `WorkstreamShellRequest` with `requestType` `show_surface` or `refresh_surface`, `targetFunctionalAgentId=<runtime agent id>`, and `targetSurfaceId=<runtime surface id>`.
- Backend shell requests accept only `show_surface`, `open_workstream`, `refresh_surface`, and `open_attention_item`, then resolve through backend authorization before returning a `SurfaceEnvelope`.
- Safe denials return a `system_message` surface owned by the target or My Account context without leaking hidden workstream or surface ids.

## Primary dashboard action markers

| Runtime owner functional agent id | Representative runtime action id | Result runtime surface id | Notes |
| --- | --- | --- | --- |
| `agent-my-account` | `action-show-my-account-dashboard` | `surface-my-account-dashboard` | Refreshes My Account summary. |
| `agent-user-admin` | `action-display-user-list` | `surface-user-admin-list` | User Admin dashboard/list entry action; dashboard opens through shell request id above. |
| `agent-agent-admin` | `action-display-agent-catalog` | `surface-agent-admin-catalog` | Agent Admin catalog/default dashboard action. |
| `agent-audit-trace` | `action-audit-trace-dashboard` | `surface-audit-trace-dashboard` | Audit/Trace dashboard action. |
| `agent-governance-policy` | `action-governance-policy-dashboard` | `surface-governance-policy-dashboard` | Governance/Policy dashboard action. |

## Follow-up mapping rule

When adding or changing a functional agent, surface, route, test, or governed-tool exposure, update this file plus `functional-agent-to-capability-map.md` and `surface-to-capability-map.md` in the same task. Do not rely on prompt text, route names, or frontend labels as authority for id translation.
