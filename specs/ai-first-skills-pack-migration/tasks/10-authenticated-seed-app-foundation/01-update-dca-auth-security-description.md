# TASK-10-001: Update DCA auth/security app-description

## Purpose

Replace placeholder DCA auth/security notes with concrete seed-app identity, authorization, trust-boundary, data-protection, and agent-permission semantics derived from `examples/poc-user-auth-onboarding/` and adapted to the AI-first DCA app description.

## Required reads

- `AGENTS.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/examples/ai-first-dca-app-description/README.md`
- `docs/examples/ai-first-dca-app-description/app-description/40-auth-security/README.md`
- `docs/examples/ai-first-dca-app-description/app-description/55-ui/ui-surfaces.md`
- `docs/examples/ai-first-dca-app-description/app-description/60-generation/implementation-slices.md`
- `examples/poc-user-auth-onboarding/AI_REVIEW_NOTES.md`
- `examples/poc-user-auth-onboarding/docs/frontend-with-akka-backend.md`
- `examples/poc-user-auth-onboarding/docs/AI_AGENT_FIRST_APP_SECURITY.md`
- `docs/security-workos-auth-and-admin.md`

## Scope

- Add concrete auth/security app-description files under `40-auth-security/`.
- Define WorkOS authentication, local Akka authorization, `/api/me`, roles/scopes, tenant/customer boundaries, admin bootstrap, invite/activation/disable behavior, forbidden outcomes, sensitive-data rules, and AI-first agent/tool authority boundaries.
- Link security semantics to UI, observability/audit, and generation slices.

## Non-goals

- No code implementation.
- No production compliance framework.
- No acceptance of optional impersonation without marking it as an explicit blocked/decision item.

## Skills

- `app-descriptions`
- `app-description-auth-security`
- `ai-first-saas`
- `ai-first-saas-policy-governance`
- `ai-first-saas-audit-trace`

## Expected outputs

- `docs/examples/ai-first-dca-app-description/app-description/40-auth-security/identity-and-trust.md`
- `docs/examples/ai-first-dca-app-description/app-description/40-auth-security/authorization-rules.md`
- `docs/examples/ai-first-dca-app-description/app-description/40-auth-security/agent-permissions.md`
- `docs/examples/ai-first-dca-app-description/app-description/40-auth-security/data-protection.md`
- `docs/examples/ai-first-dca-app-description/app-description/40-auth-security/boundary-and-surface-rules.md`
- Updated `40-auth-security/README.md`.

## Required checks

- Verify no auth/security file treats frontend navigation as authorization.
- Verify backend secrets are explicitly forbidden in frontend env/build assets.
- Verify AI-first agent/tool authority requires mechanical enforcement and audit.

## Done criteria

- The DCA seed app has enough auth/security meaning for downstream implementation without guessing.
