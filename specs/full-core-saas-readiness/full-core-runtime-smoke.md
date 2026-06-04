# Full-Core Runtime Smoke

- task: TASK-FCSR-07-001
- date: 2026-06-04
- scope label: full core, with billing implementation deferred by the gap contract
- result: local runtime/API/UI smoke evidence recorded; production-provider smokes remain blocked until real backend-only configuration is supplied

## Evidence summary

The local validation pass exercised the current Akka/API/UI implementation through the repository's normal test and build commands. It supports readiness for the implemented full-core foundation scope at local/test scope, while preserving explicit blockers for live WorkOS/AuthKit, live Resend, and billing implementation. TASK-FCSR-08-003 later supplied backend-only model-provider environment variables and passed the live model-backed workstream-agent smoke.

## Checks run

| Check | Result | Evidence |
| --- | --- | --- |
| `mvn test` | passed | 285 tests, 0 failures, 0 errors, 1 skipped; Akka TestKit started local runtime and discovered HTTP endpoints for `/api/me`, workstream APIs, admin APIs, and frontend routes. |
| `npm --prefix frontend test -- --run` | passed | 139 frontend contract tests passed, covering WorkOS/AuthKit frontend fail-closed behavior, no backend secret references, canonical workstream shell, User Admin, Agent Admin, Audit/Trace, Governance/Policy, My Account, action affordances, and browser-safe rendering. |
| `npm --prefix frontend run typecheck` | passed | TypeScript check completed with no errors. |
| `npm --prefix frontend run build` | passed | Vite production build completed into `src/main/resources/static-resources`. |
| `tools/scan-ai-first-saas-static-assets.sh src/main/resources/static-resources` | passed | Built static assets contained no configured backend secret markers. |
| `tools/prove-workstream-icons-v0.sh` | passed after TASK-FCSR-08-005 repair | The optional icon proof now targets `src/main/java/ai/first/application/foundation/identity/MeResponse.java` and no longer reports a false blocker against the current foundation identity/API package layout. |

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
- Live model-backed workstream-agent provider smoke passed in TASK-FCSR-08-003 with backend-only provider environment variables; local tests continue to verify fail-closed behavior when provider configuration is absent.
- Billing implementation remains deferred by the readiness gap contract. Current smoke only preserves the billing-boundary invariant that billing/subscription metadata must not grant tenant application-data access.
- Timer-backed invitation reminder scheduling remains deferred from the invitation task and is not claimed by this smoke.
- Optional `tools/prove-workstream-icons-v0.sh` validation was repaired in TASK-FCSR-08-005 and now passes against the current package layout.

## Readiness conclusion

The current app is locally smoke-validated for the selected full-core foundation scope except billing implementation and remaining live WorkOS/AuthKit and Resend provider smokes. Live model-backed workstream-agent provider smoke passed in TASK-FCSR-08-003. Readiness may be described as `full-core local/test scope validated; live model-provider workstream-agent smoke passed; remaining production-provider readiness blocked by missing WorkOS/AuthKit and Resend external configuration; billing implementation deferred`.
