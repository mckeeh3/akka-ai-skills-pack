# 40 Auth Security

This layer defines the DCA vertical reference security contract. It is aligned with `../10-capabilities/01-secure-tenant-user-foundation.md`: WorkOS/AuthKit authenticates browser humans, while Akka-owned local `Account`, `Membership`, `Role`, `Permission/Capability`, `Invitation`, support-access, and selected `AuthContext` state authorizes all foundation and DCA capabilities.

Current files:

- `identity-and-trust.md` — WorkOS/AuthKit authentication, JWT-protected APIs, Akka-owned authorization state, `/api/me`, invite-only first-login linking, startup bootstrap limits, and trust boundaries.
- `authorization-rules.md` — foundation roles, DCA extension-role mappings, backend authorization defaults, capability-oriented surface matrix, denial behavior, admin API families, and optional impersonation guardrails.
- `agent-permissions.md` — mechanical authority boundaries for agents, tools, workflows, action-boundary judging, admin-assistant agents, supplies autopilot, governed policy/prompt changes, and audit traces.
- `data-protection.md` — sensitive data classes, frontend/backend secret separation, response/log minimization, retention/trace visibility, deny-by-default cases, and linked tests.
- `foundation-onboarding-admin-boundaries.md` — mandatory invitation lifecycle, auditable admin operations, support-access rules, and SaaS Owner billing/subscription boundary.
- `boundary-and-surface-rules.md` — public static routes versus protected `/api/...` routes, frontend UX boundaries, backend enforcement boundaries, integration boundaries, support-access, and implementation reference notes.

Core rules:

- Frontend navigation, hidden buttons, cached `/api/me`, JWT role claims, prompt text, and tool descriptions are never authorization controls.
- Every protected route, component command, view query, stream, workflow action, consumer side effect, timer action, agent tool, and generated UI action must be backed by server-side authorization.
- Tenant/customer scope and support-access limits are enforced mechanically and audited.
- Privileged self-registration from WorkOS claims alone is forbidden; privileged access requires a valid invitation, accepted membership policy, or bounded audited bootstrap path.
- Invitation delivery, resend, revoke/cancel, expiry, acceptance, delivery failure visibility, support-access, billing-boundary, and consequential admin activity emit `AdminAuditEvent` and/or work/decision trace facts.

Source reference: use `templates/ai-first-saas-starter/` for the canonical generated starter foundation and the current root security reference packages under `src/main/java/com/example/**/security` for executable WorkOS/AuthKit and local-authorization patterns; these files are not a drop-in production security system.

Linked layers:

- Capability: `../10-capabilities/01-secure-tenant-user-foundation.md` is the foundation contract these files refine.
- UI: `../55-ui/ui-surfaces.md` may hide or reveal navigation from `/api/me`, but every `/api/...` operation remains backend-authorized.
- Observability: `../50-observability/audit-trace-and-outcomes.md` defines audit/work/decision trace facts that security-sensitive operations must emit.
- Generation: `../60-generation/implementation-slices.md` uses this layer as the implementation contract for the authenticated reference foundation.
- Tests: `../30-tests/` should cover missing JWT, `/api/me`, uninvited identities/no privileged self-registration, disabled accounts, cross-scope denial, role/scope denial, invitation lifecycle/delivery failure, support-access boundaries, billing-safe redaction, frontend-secret absence, admin audit emission, and agent/tool boundary denial.
