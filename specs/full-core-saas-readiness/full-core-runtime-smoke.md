# Full-Core Runtime Smoke

- task: TASK-FCSR-07-001
- date: 2026-06-04
- scope label: full core, with billing implementation deferred by the gap contract
- result: local runtime/API/UI smoke evidence recorded; production-provider smokes remain blocked until real backend-only configuration is supplied

## Evidence summary

The local validation pass exercised the current Akka/API/UI implementation through the repository's normal test and build commands. It supports readiness for the implemented full-core foundation scope at local/test scope, while preserving explicit blockers for live WorkOS/AuthKit, live Resend, live model-provider calls, and billing implementation.

## Checks run

| Check | Result | Evidence |
| --- | --- | --- |
| `mvn test` | passed | 285 tests, 0 failures, 0 errors, 1 skipped; Akka TestKit started local runtime and discovered HTTP endpoints for `/api/me`, workstream APIs, admin APIs, and frontend routes. |
| `npm --prefix frontend test -- --run` | passed | 139 frontend contract tests passed, covering WorkOS/AuthKit frontend fail-closed behavior, no backend secret references, canonical workstream shell, User Admin, Agent Admin, Audit/Trace, Governance/Policy, My Account, action affordances, and browser-safe rendering. |
| `npm --prefix frontend run typecheck` | passed | TypeScript check completed with no errors. |
| `npm --prefix frontend run build` | passed | Vite production build completed into `src/main/resources/static-resources`. |
| `tools/scan-ai-first-saas-static-assets.sh src/main/resources/static-resources` | passed | Built static assets contained no configured backend secret markers. |
| `tools/prove-workstream-icons-v0.sh` | blocked/stale optional tool | The script still expects `src/main/java/ai/first/application/security/MeResponse.java`, while the current app uses the foundation security/API package layout. This stale icon proof does not block the required secret-boundary scan but should be repaired separately if retained. |

## Runtime/API/UI coverage observed

- Auth and identity: backend tests covered `/api/me`, selected `AuthContext`, disabled/forbidden states, WorkOS JWT/config resolution seams, and provider-secret fail-closed behavior at local scope.
- Invitations/email: backend tests covered invitation lifecycle, captured local/test email outbox behavior, resend/revoke/accept flows, idempotency, delivery failure handling, and Resend production config fail-closed errors when required backend environment variables are absent.
- User Admin: backend and frontend tests covered dashboard/list/detail/access-review/support-access/admin-audit surfaces and governed actions through the workstream/API contracts.
- Managed-agent foundation: backend tests covered agent definitions, governed prompt/skill/reference/tool-boundary records, seed loading, runtime resolution, denied loads/tool-boundary failures, prompt/skill/reference/model trace seams, and provider fail-closed behavior.
- Audit/Trace and Governance/Policy: backend and frontend tests covered search/detail/timeline/investigation/failure-evidence, proposal/simulation/decision/activation/rollback/outcome-note actions, scoped redaction/denial expectations, trace links, and safe UI rendering.
- Frontend workstream shell: contract tests covered canonical `frontend/src/workstream/**` runtime path, persistent composer, structured surfaces, safe markdown/system-message rendering, stale/realtime semantics, action feedback, accessibility/responsive markers, and no normal runtime fixture imports.
- Tenant and authorization invariants: focused tests across backend and frontend contracts covered tenant/customer scope, role/scope denial, disabled-user denial, support-access constraints, last-admin protection, audit/work-trace emission, and no frontend-only authorization claim.

## Remaining blockers and deferred scope

- Live WorkOS/AuthKit provider smoke remains blocked until backend-only WorkOS issuer/audience/provider configuration and a real AuthKit app are supplied.
- Live Resend provider smoke remains blocked until backend-only Resend API key and sender/domain configuration are supplied.
- Live model-backed worker/agent provider smoke remains blocked until a real model provider configuration, runtime tool-boundary configuration, and approved provider credentials are supplied. Local tests verify fail-closed behavior rather than making live model calls.
- Billing implementation remains deferred by the readiness gap contract. Current smoke only preserves the billing-boundary invariant that billing/subscription metadata must not grant tenant application-data access.
- Timer-backed invitation reminder scheduling remains deferred from the invitation task and is not claimed by this smoke.

## Readiness conclusion

The current app is locally smoke-validated for the selected full-core foundation scope except billing implementation and live external-provider smokes. Readiness may be described as `full-core local/test scope validated; production-provider readiness blocked by missing external configuration; billing implementation deferred`.
