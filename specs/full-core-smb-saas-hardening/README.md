# Full-Core SMB SaaS Hardening

## Purpose

Drive the packaged five-core AI-first SaaS starter from v0 into a functionally complete SMB-ready core baseline that downstream projects can extend with domain-specific workstreams.

This is the next major program for the skills pack. It should harden the core workstreams, surfaces, agents, internal worker agents, deterministic services, visual design, runtime behavior, tests, docs, and skills-pack guidance by using the starter template as the executable proving ground.

## Target product stance

The target is a real small and medium business SaaS baseline, not an enterprise platform and not an old-school CRUD admin console.

The canonical application model is:

```text
secure SaaS foundation
→ AI-first workstream shell
→ role-authorized functional/context-area agents
→ rich structured surfaces and dashboards
→ governed capabilities
→ request/response Akka Agent turns for interactive work
→ internal worker agents, especially Akka AutonomousAgent tasks, for tedious/background work
→ deterministic services for mechanical policy/lifecycle/projection/audit behavior
→ durable traces, human supervision, approvals, and outcomes
```

The workstream and surface UX/UI architecture is the only product architecture for this effort. Conventional pages/routes may support deep links and implementation, but they must not become the primary app model.

## Background

Completed prerequisite work:

- `specs/five-core-workstream-v0-starter/`
- `specs/production-ready-five-core-v0/`
- `specs/workstream-akka-agent-runtime/`
- `specs/five-core-workstreams-v0-plan/`
- `specs/my-account-workstream-v0/`
- `specs/user-admin-workstream-v0/`
- `specs/agent-admin-workstream-v0/`
- `specs/audit-trace-workstream-v0/`
- `specs/governance-policy-workstream-v0/`
- `specs/core-prd-workstream-reconciliation/`
- `specs/release-readiness-after-five-core-v0/`

The v0 starter is validated and release-ready. This program expands beyond v0 into SMB functional completeness.

## Scope

Plan and then execute a sequence of mini-project sprint waves for:

1. full-core SMB baseline, visual/UX standards, and wave plan;
2. My Account full-core;
3. User Admin full-core;
4. Agent Admin full-core;
5. Audit/Trace full-core;
6. Governance/Policy full-core;
7. cross-workstream polish, validation, documentation, skills-pack fixes, and release readiness.

Each workstream should become usable for real SMB operations at its stated scope. For example, User Admin should support managing users, invitations, memberships, roles/capabilities, disabled/reactivated users, access review, admin-risk help, and audit visibility in a way a small/medium business could actually use.

## Non-goals

- Do not target enterprise-only scope such as full SSO administration consoles, SIEM integrations, legal hold, complex procurement/compliance suites, or multi-region enterprise operations unless explicitly selected as lightweight later hardening.
- Do not replace the workstream/surface architecture with page-first CRUD screens.
- Do not mark model-backed behavior complete with deterministic/model-less normal runtime substitutes.
- Do not build domain-specific business workstreams until the core baseline is in a good place.
- Do not hide skills-pack gaps discovered during implementation; record or fix them explicitly.

## Operating principles

- Use the starter template as the primary executable baseline: `templates/ai-first-saas-starter/`.
- Keep root `frontend/` synchronized when starter frontend source changes require it.
- Treat Akka local runtime validation as production-like validation.
- Request/response workstream turns must use the governed Akka Agent runtime path.
- Internal worker agents should do meaningful background/tedious work where lifecycle semantics justify them; prefer Akka `AutonomousAgent` for durable task-oriented internal work.
- Deterministic services should own mechanical authorization, validation, lifecycle, projection, trace normalization, and idempotency.
- Visual quality is a first-class acceptance criterion: dashboards and surfaces must be attractive, legible, responsive, accessible, and appropriate for a leading-edge AI-first app.
- Execute one bounded task per fresh harness session.

## Affected repository areas

- `templates/ai-first-saas-starter/`
- `frontend/`
- `docs/`
- `skills/`
- `pack/`
- `tools/`
- child specs under `specs/full-core-smb-*` or equivalent

## Execution model

This umbrella mini-project plans and coordinates child mini-projects. It should not directly implement all full-core work in one queue. Instead, it defines the SMB baseline, wave plan, and child project creation tasks.

Recommended child mini-project families:

- `full-core-smb-baseline-and-ux`
- `full-core-smb-my-account`
- `full-core-smb-user-admin`
- `full-core-smb-agent-admin`
- `full-core-smb-audit-trace`
- `full-core-smb-governance-policy`
- `full-core-smb-polish-release`

Additional mini-projects may be appended as implementation teaches us more.

## Read order for future task sessions

1. `AGENTS.md`
2. `skills/README.md`
3. this mini-project's `README.md`
4. this mini-project's `conversation-capture.md`
5. this mini-project's `pending-tasks.md`
6. selected sprint/backlog/task brief
7. relevant existing five-core v0 specs and starter docs named by the selected task
8. only the exact skill/source files needed by that task

## Done state

This umbrella mini-project is complete when:

- an SMB full-core baseline definition exists;
- a workstream-by-workstream full-core capability and surface outline exists;
- visual/UX quality standards are explicit;
- request/response, AutonomousAgent/internal-worker, and deterministic-service selection rules are explicit for full-core work;
- a wave plan and child mini-project queue plan exists;
- child mini-project scaffolds are created for the first implementation wave;
- terminal verification confirms the program is ready to proceed through child mini-projects without guessing.
