# Security and governance consistency review

## Scope

Reviewed the capability-first migration against the mandatory secure AI-first SaaS foundation and core SaaS foundation guidance.

Primary task: `TASK-06-002` from `pending-tasks.md`.

## Reviewed sources

- `AGENTS.md`
- `skills/README.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/capability-first-backend-architecture.md`
- `docs/core-ai-first-saas-foundation.md`
- `docs/core-saas-identity-tenancy-admin.md`
- `docs/core-saas-owner-tenant-billing.md`
- `docs/security-pattern-selection.md`
- `docs/security-review-checklist.md`
- `skills/core-saas-foundation/SKILL.md`
- selected security and endpoint skills found by targeted search

## Review result

The capability-first migration preserves the mandatory security and governance baseline:

- generated SaaS app paths route through `core-saas-foundation` before app-specific domain work;
- capability contracts require actors/callers, selected `AuthContext`, tenant/customer scope, roles/capabilities, schemas, side effects, idempotency, policy/approval, audit/trace, exposure surfaces, and tests;
- agent tools, MCP tools, browser routes, workflows, timers, consumers, and endpoints are treated as selected exposure/execution surfaces rather than authority roots;
- prompt text, frontend navigation, route names, hidden fields, and tool descriptions are consistently rejected as authorization controls;
- the secure foundation includes WorkOS/JWT authentication seam, Akka-owned authorization state, complete Invitation lifecycle, `/api/me`, support-access, subscription/billing boundary, admin read models, admin audit, tenant isolation, and first-slice security tests;
- high-impact security, billing, governance, policy, data-export, cross-tenant, and external-side-effect capabilities default to human approval or documented bounded autonomy.

## Fixes applied

Small cleanup edits were applied to older security reference docs so they now explicitly preserve capability-first semantics:

1. `docs/security-pattern-selection.md`
   - added a capability-first rule before choosing security implementation details;
   - expanded common web app security flow to build `AuthContext`, enforce named capability authority, and audit protected access/side effects;
   - clarified testing minimum to include tenant isolation, disabled users, role/scope denials, audit, approval-gated consequential actions, and exposure-surface consistency.
2. `docs/security-review-checklist.md`
   - added a capability/governance section covering capability id, shared authority semantics across surfaces, prompt/frontend/tool-description non-authority, approvals, idempotency, audit/work traces, and deterministic agent-tool testing;
   - strengthened authorization, administration, and tests checklist items for tenant isolation, support-access, invitations, and frontend secret boundaries.

## Residual findings

No blocking security/governance inconsistencies were found.

Non-blocking observation: archived inbox material under `specs/ai-first-skills-pack-migration/archive/inbox/` still contains older draft wording, but repository guidance already marks it as provenance only. No cleanup is required for this migration task.

## Verification searches

Targeted searches checked for:

- broad paths bypassing `core-saas-foundation` or capability modeling;
- frontend-only, prompt-only, route-name, hidden-field, JWT-presence, or tool-description authorization patterns;
- unsafe raw token exposure, global super-admin language, or broad MCP/all-tools exposure;
- preservation of tenant/customer scope, permissions, audit, approval, and tests in doctrine/routing/security docs.

## Status

Security and governance alignment is verified for Sprint 6.
