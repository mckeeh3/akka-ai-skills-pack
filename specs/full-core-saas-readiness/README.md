# Full-Core SaaS Readiness

## Purpose

Create a durable mini-project for closing the runnable AI-first SaaS core app's remaining full-core readiness gaps after the workstream design/implementation alignment initiative completed.

The goal is to move from a partially implemented five-core workstream starter toward a validated full-core SaaS foundation through bounded, runtime-proven tasks.

## Source discussion / trigger

After `specs/workstream-design-implementation-alignment/` completed, the readiness docs still identified full-core gaps:

- production WorkOS/AuthKit configuration and fail-closed local validation;
- complete invitation onboarding with Resend and captured local/dev/test outbox;
- complete User Admin structured surfaces for users, invitations, roles/memberships, access review, support access, and admin audit;
- complete Agent Admin lifecycle for governed agent definitions, prompts, skills, references, manifests, tool boundaries, proposals, approvals, and traces;
- searchable Audit/Trace investigation surfaces with scoped redaction/export rules;
- Governance/Policy workflows, approval gates, impact analysis, review surfaces, and policy-change controls;
- support-access and billing-boundary semantics where the target product requires them;
- full tenant isolation, forbidden access, disabled-user, role/scope denial, audit, UI, accessibility, and runtime smoke coverage.

The user asked what comes next and accepted creating a full-core readiness queue.

## Scope

This mini-project targets root core app readiness across:

- `app-description/**`
- `src/main/java/ai/first/**`
- `src/test/java/ai/first/**`
- `frontend/**`
- `docs/**`
- `tools/**`

## Non-goals

- Do not add domain-specific/business workstreams.
- Do not replace the workstream-first architecture with page-first CRUD.
- Do not claim production readiness unless local runtime/API/UI evidence proves it at the stated scope.
- Do not use deterministic/demo/model-less normal-runtime behavior for auth, workstream agents, protected capabilities, provider-backed calls, audit/work traces, or durability.
- Do not commit unrelated untracked `.agents/**` assets.

## Execution model

Execute one task per fresh harness context. Each task must update the queue, run required checks or mark blocked with a precise reason, and make one focused commit before being marked `done`.

## Read order for future task sessions

1. `AGENTS.md`
2. `app-description/app.md` and `app-description/domains/core-starter/realization/traceability.md`
3. `specs/full-core-saas-readiness/full-core-readiness-verification.md`
4. `specs/full-core-saas-readiness/README.md`
5. `specs/full-core-saas-readiness/conversation-capture.md`
6. `specs/full-core-saas-readiness/pending-tasks.md`
7. selected sprint/backlog/task brief
8. task-specific app-description/source/test files

## Sprint sequence

1. Sprint 01: Readiness contract and validation baseline.
2. Sprint 02: Identity, invitation, and User Admin foundation completion.
3. Sprint 03: Managed-agent, Audit/Trace, and Governance/Policy depth.
4. Sprint 04: Runtime smoke and full-core readiness verification.

## Done state

This mini-project is complete when either:

1. full-core SaaS readiness is proven through real local Akka/API/UI runtime checks for the selected full-core scope; or
2. remaining gaps are explicitly documented, blocked/deferred with accepted reasons, and a new bounded follow-up queue exists for unresolved work.

A full-core-ready state requires:

- WorkOS/AuthKit local/prod config validation and frontend secret-boundary enforcement;
- complete invitation onboarding with Resend/captured outbox, expiry/reminder behavior, and lifecycle tests;
- User Admin surfaces/actions for users, invitations, roles/memberships, access review, support access, and admin audit;
- managed-agent lifecycle, governed prompt/skill/reference/manifest/tool-boundary flows, proposals/approvals, and traces;
- Audit/Trace searchable investigation with scoped redaction/export rules;
- Governance/Policy proposal, approval, impact, activation/rollback, and review surfaces;
- tenant isolation, forbidden access, disabled-user, role/scope denial, idempotency, audit/work-trace, UI, accessibility, and runtime smoke coverage.

## Open concerns

- Some production provider configuration can only be validated as fail-closed locally unless actual secrets are supplied.
- Billing-boundary scope may need a product decision; record as deferred only if the current full-core target explicitly excludes billing behavior.
- Existing runtime pieces may already satisfy parts of this list; the first task must inventory evidence before implementation tasks proceed.
