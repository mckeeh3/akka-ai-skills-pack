# Fullstack named-surface validation

## Purpose

`TASK-STARTER-08-008` tightens the starter fullstack validation so final acceptance cannot pass by proving only a generic scaffold build. The validation must prove the named five-core workstream shell, User Admin dashboard → list → account vertical, realtime/stale behavior, static asset hosting, protected API separation, and audit/trace evidence markers.

## Validation hook

`tools/validate-ai-first-saas-starter-fullstack.sh` now runs `validate_named_surface_and_shell_contracts` after scaffold rendering and before the normal backend/frontend test suite.

The hook verifies rendered scaffold source and tests for:

- public static frontend routes: `/`, `/ui`, `/workstream`, `/assets/**`;
- protected User Admin backend APIs for dashboard, user list/search, account detail, invitation resend, and admin audit events;
- backend API tests proving dashboard/account payloads, a safe User Admin mutation, audit output, forbidden/missing/cross-context denials, and no raw invitation token leakage;
- backend workstream-service tests proving shell request handling, `surface-user-admin-dashboard`, `surface-user-admin-list`, `surface-user-admin-detail-admin`, safe mutation result surfaces, audit events, and five core workstream runtime coverage;
- frontend fixtures and contract tests for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy starter surfaces;
- frontend capability markers for User Admin, Agent Admin, Audit/Trace, Governance/Policy, and My Account `core.access.me` coverage;
- shell contract coverage for the role-authorized rail, main workstream panel, persistent composer, backend API client, backend realtime client, `/api/workstream/events`, `surface.stale`, and `surface.reconnected` behavior;
- fixture-client isolation from production `main.tsx`.

The existing full validation still runs Maven tests, frontend tests, typecheck, build, static asset scan, bundle analysis, and optional real-provider smoke. The new hook makes those checks fail early when named surface/API/realtime proof disappears from the rendered scaffold.

## Acceptance mapping

| Requirement | Validation evidence |
|---|---|
| Role-authorized functional-agent rail, persistent composer, context/authority shell | `workstream-shell.contract.test.mjs`, production `main.tsx`, rendered backend shell-request tests |
| User Admin dashboard/list/detail load from real backend APIs/components | `AdminEndpoint.java`, `AdminEndpointIntegrationTest.java`, `WorkstreamServiceTest.java` markers plus full Maven execution |
| Safe mutation or decision-card-producing action emits audit/trace | invitation/member/status/action tests and `auditEvents()` markers plus full Maven execution |
| Realtime/stale/reconnect and protected route separation | `HttpWorkstreamRealtimeClient.ts`, frontend realtime tests, public static route markers, protected `/api/...` route markers |
| Named Access/Profile, User Admin, Agent Admin, Audit/Trace, Governance/Policy surfaces/capabilities | frontend surface fixtures, My Account core access test, workstream runtime coverage test |

## Residual note

This is validation hardening, not a new app feature. It records proof markers that the rendered scaffold already exercises through its normal Maven/frontend/fullstack validation path.
