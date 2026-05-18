# Boundary and Surface Rules

## Purpose

Define route, UI, backend, integration, and automation boundaries for the DCA vertical reference. These surfaces expose or support capabilities; they do not define authorization by themselves.

## Route boundaries

Public routes:

```text
/              frontend app entry
/assets/**     generated React/Vite assets
/favicon.ico   generated/static browser asset if present
```

Protected routes:

```text
/api/...
/api/.../stream or SSE routes
/api/... WebSocket routes if introduced
admin, audit, decision, policy, support-access, and DCA domain API families
```

Rules:

- Static frontend assets may be public.
- Backend API, stream, WebSocket, admin, audit, decision, policy, support-access, agent-tool, and DCA domain routes require JWT and backend authorization unless explicitly documented otherwise.
- Browser API calls should use same-origin relative `/api/...` URLs when Akka hosts the frontend.
- Avoid overlapping wildcard static routes. Prefer explicit app entry routes, hash/internal navigation, or documented non-wildcard routes.
- Internal-only endpoints, MCP surfaces, consumers, timers, and workflow actions still require service/component ACLs, selected scope, provenance, and audit where consequential.

## Frontend UX boundaries

- Role-aware navigation and hidden actions are UX conveniences, never authorization.
- `/api/me` may drive visible navigation and action affordances, but backend handlers must still re-check every protected operation.
- Unauthorized, forbidden, disabled-account, pending-invite, loading, empty, stale, offline/reconnect, and error states are required UI states.
- Context selection must make the active tenant/customer scope visible and prevent accidental cross-scope action in the UI, while backend scope checks remain authoritative.
- Decision cards must show policy/evidence/risk/confidence/impact/trace context before approve, reject, suppress, or escalate actions.
- Admin UI surfaces must distinguish SaaS Owner, Tenant Admin, Customer Admin, Auditor, support-access, and DCA operational roles.
- Users must understand whether they are acting as themselves or, if a future support/impersonation feature is approved, as an effective user under audited support-access.

## Backend enforcement boundaries

- Endpoint methods must call a central authorization helper or component boundary for account status, membership, role, permission/capability, support-access, and tenant/customer scope checks.
- Component commands, view queries, streams, workflow actions, consumers, timers, and agent tools must either receive or resolve an `AuthContext` and enforce capability-specific authority.
- List/query endpoints must filter by tenant/customer scope server-side, with pagination and redaction before returning rows.
- Mutation endpoints must validate target scope, allowed role transitions, idempotency, approval requirements, and last-admin protection server-side.
- Admin, support-access, billing-boundary, policy, high-impact DCA, and AI-first decisions must write audit/work/decision trace facts.
- Frontend UI state, cached `/api/me` responses, hidden buttons, route names, prompt instructions, and hidden form fields must never be treated as proof of authority by backend handlers.

## Integration boundaries

- WorkOS authenticates; Akka authorizes.
- Email invite delivery is an adapter boundary. Production readiness uses Resend (resend.com) delivery by default; alternate production providers require an accepted override decision. Local/dev/test must use an explicit captured outbox adapter with visible/auditable delivery failures, delivery attempts, provider/outbox ids, resend/revoke/expiry state, and no raw token exposure outside the delivery/acceptance boundary.
- SaaS Owner billing/subscription integration may access billing-safe Tenant metadata only and must not expose Tenant application data, Customer service data, DCA telemetry, agent work traces, user settings, or non-billing profile details.
- External DCA, ERP, supplier, shipping, meter, billing, and service integrations remain deferred until their capability contracts define auth, data minimization, idempotency, retries, redaction, and audit.
- Agent tools must be narrower than broad administrative APIs whenever possible and must enforce the same backend capability contract as browser/API exposure.
- MCP or service-to-service surfaces, if added later, must expose only selected tools/resources, use ACL/JWT/service identity, filter allowed tools by caller and scope, and audit remote access.

## Support-access boundary

Support access is a Tenant-created, time-limited, reasoned, visible, revocable, and audited Tenant-scoped membership for SaaS Owner personnel. It is not impersonation by default and not a global super-admin bypass. Support-access create, use, extension, expiry, revocation, denied attempts, and review-agent recommendations must cite tenant scope, reason, actor, target surface, expiry, permission checked, decision-card link where present, and correlation id in audit/work traces.

## PoC adaptation note

The `examples/poc-user-auth-onboarding/` ideas remain implementation guidance for WorkOS/AuthKit, React/Vite hosting, and same-origin APIs. The DCA reference must preserve the current secure foundation capability, selected UI style guide, AI-first supervision surfaces, tenant/customer boundaries, and audit/trace requirements rather than copying PoC policies as production-ready rules.
