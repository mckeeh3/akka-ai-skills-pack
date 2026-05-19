# Security review checklist

Use before completing security-sensitive Akka changes.

## Route exposure

- [ ] Public static frontend routes are separated from protected `/api/...` routes.
- [ ] Protected browser APIs have `@JWT`.
- [ ] Internal-only routes use ACL guidance and are not accidentally internet-facing.
- [ ] No broad wildcard route creates unintended exposure or conflicts.

## JWT and identity

- [ ] Frontend sends `Authorization: Bearer <token>` for protected APIs.
- [ ] Endpoint reads claims only after `@JWT` validation.
- [ ] Issuer/audience/static claims are configured when the contract is known.
- [ ] Production does not rely on local unsigned JWT behavior.
- [ ] Integration tests inject representative bearer tokens.

## WorkOS/frontend secrets

- [ ] WorkOS/AuthKit is the browser user auth service for generated apps.
- [ ] `VITE_WORKOS_CLIENT_ID` and `VITE_WORKOS_REDIRECT_URI` are treated as public frontend build variables.
- [ ] `WORKOS_API_KEY`, optional `WORKOS_API_BASE_URL`, `WORKOS_JWT_ISSUER`, `WORKOS_JWT_AUDIENCE`, Resend keys, invite sender config, and bootstrap admin config are backend-only runtime/deployment variables.
- [ ] No backend secrets appear in `frontend/.env*` or built assets.
- [ ] WorkOS dashboard redirect/origin settings match local/deployed modes.

## Authorization

- [ ] Authentication and authorization are separate in code and docs.
- [ ] Backend loads local user/account/membership/role/capability state before protected operations.
- [ ] Frontend navigation, hidden fields, route names, prompts, and tool descriptions are not treated as authorization.
- [ ] Disabled users are rejected despite valid JWTs.
- [ ] Tenant/customer/self scope is checked on reads and writes.
- [ ] Tenant/customer isolation tests cover cross-scope command and query attempts.
- [ ] Admin APIs prevent privilege escalation.

## Capability governance

- [ ] Each protected operation/query has a capability id and named actor/caller set.
- [ ] Capability contract includes selected `AuthContext`, tenant/customer scope, required roles/permissions/capabilities, input/output DTOs, side effects, idempotency, policy/approval rule, audit/work-trace facts, exposure surfaces, and tests.
- [ ] Browser/API/tool/workflow/timer/consumer/MCP surfaces preserve the same authority, validation, approval, idempotency, tenant isolation, and audit semantics for the shared capability.
- [ ] Side-effecting agent or MCP tools require explicit permission and default to proposal/approval flows unless a documented bounded autonomy policy allows direct action.
- [ ] High-impact security, billing, policy, governance, support-access, data-export, cross-tenant, or external-side-effect capabilities require human approval or a documented autonomous policy boundary.
- [ ] Agent/tool tests use deterministic invocation patterns and verify backend enforcement rather than prompt-only compliance.

## Administration

- [ ] `/api/me` returns a browser-facing DTO, not internal entity state.
- [ ] Complete Invitation lifecycle exists: create, delivery/captured outbox, resend, revoke/cancel, expiry, acceptance, delivery failure visibility, idempotency, and no raw-token leakage.
- [ ] Invite and first-login linking are idempotent and require a valid invitation, acceptance context, or explicit membership policy.
- [ ] Startup admin bootstrap is idempotent, validates malformed config, uses canonical foundation roles, and does not create a permanent bypass.
- [ ] Support-access is Tenant-created, time-limited, auditable, revocable, visible to Tenant admins, and does not create a SaaS Owner super-admin path.
- [ ] Admin actions emit required AdminAuditEvent records.
- [ ] First-slice admin read models exist: UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView.
- [ ] Admin/audit/search query paths support required filters where applicable: actor, target user, tenant, customer, role, membership status, invitation status, delivery status, action type, risk, due/expiry time, and time range.
- [ ] Admin view endpoints authorize every query with AuthContext, reject or hide cross-scope access, and do not rely on frontend filtering as the security boundary.
- [ ] Admin view DTOs redact raw invitation tokens, provider ids/secrets, support details, policy evidence, and tenant/customer data outside caller authority.
- [ ] Admin UI surfaces expose audit search, invitation queue, access review queue, stale/dormant access warnings, and agent-generated admin recommendations.
- [ ] Tests cover allowed and forbidden admin operations, disabled users, role/scope denial, query authorization, cross-scope filtering, redaction, pagination, stale invite/access-review queue correctness, support-access lifecycle, last-admin protection, approval-gated admin decisions, frontend secret boundaries, and audit trace completeness.
