# AI-First SaaS Starter App Template Plan

This planning package defines the migration from scattered executable examples and fixture-backed UI references to a canonical, installable, **fully functional AI-first SaaS starter app** shipped with the skills pack.

The starter app is intended to be the preferred foundation for new generated applications: users install or scaffold a working secure AI-first SaaS app, then extend it through app-description, capability, Akka component, and workstream UI tasks.

## Why this migration exists

The repository now has strong doctrine, app-description guidance, workstream UI design, React/Vite implementation modules, and many focused Akka examples. The remaining confidence gap is an end-to-end full-core implementation that proves the whole pack can generate and extend a real app without relying on old DCA/static examples or fixture-only frontend seams.

The new target is:

```text
installable skills pack
→ optional starter-app scaffold for empty/new target projects
→ runnable Akka + React/Vite/TypeScript full-core SaaS app
→ secure SaaS foundation and governed agent runtime
→ real backend APIs wired to canonical workstream UI
→ tests proving auth, tenancy, admin, audit, agent governance, frontend, and packaging behavior
→ extension workflow for app-specific requirements
```

## Target outcome

The migration is complete when the repository contains:

- a canonical starter app template under a pack-resource/template location, not mixed with legacy examples;
- a documented install/scaffold mode that can copy the starter into a target project only when explicitly requested or when creating a new empty project;
- a runnable full-core foundation with Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission/Capability, Invitation onboarding, `/api/me`, authorization, audit, and tenant-isolation tests;
- governed runtime agent foundation with seed documents, prompt/skill/manifest/tool-boundary records, deterministic assembly/readSkill contracts, traces, and Agent Admin surfaces;
- canonical workstream frontend wired to real backend endpoints for Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy instead of fixture-only clients;
- packaging docs and skills that route new users toward extending the starter app while preserving skills-only installs for existing projects;
- old DCA/static seed assets clearly quarantined, archived, or removed from canonical generation paths;
- final end-to-end acceptance tests and a completion summary.

## Execution model

- Execute one task per fresh harness context.
- Use `pending-tasks.md` as the durable queue.
- Select the first `pending` task whose dependencies are satisfied.
- Read this README, `conversation-capture.md`, the selected sprint, backlog, queue entry, and task brief before editing.
- Do not perform broad opportunistic rewrites outside the selected task scope.
- Each task must end with one git commit containing only that task's intended changes and queue-status update.
- Record the commit message in task notes; record commit hash only when practical without amending the same commit.
- Preserve task IDs. Supersede obsolete tasks instead of renumbering or deleting them.

## Read order for future sessions

1. `AGENTS.md`
2. `skills/README.md`
3. `docs/ai-first-saas-application-architecture.md`
4. `docs/capability-first-backend-architecture.md`
5. `docs/core-ai-first-saas-foundation.md`
6. `docs/core-saas-identity-tenancy-admin.md`
7. `docs/agent-workstream-application-architecture.md`
8. `docs/workstream-ui-reference-architecture.md`
9. `specs/core-app-full-stack-readiness/README.md`
10. this file
11. selected sprint spec under `sprints/`
12. selected backlog under `backlog/`
13. selected task entry in `pending-tasks.md`
14. selected task brief under `tasks/`

## Sprint sequence

1. `sprints/01-template-scope-inventory-sprint.md` — define starter-app scope, packaging stance, canonical template location, and legacy-code disposition.
2. `sprints/02-foundation-backend-sprint.md` — implement or relocate the runnable secure SaaS backend foundation for the starter app.
3. `sprints/03-workstream-api-frontend-sprint.md` — wire canonical workstream UI to real starter backend APIs and replace fixture-only confidence with contract/integration tests.
4. `sprints/04-agent-governance-sprint.md` — complete governed runtime agent seed documents, Agent Admin, governance/policy, trace, and behavior-editing foundation.
5. `sprints/05-packaging-install-sprint.md` — package the starter app as an explicit scaffold/init mode alongside skills-only installation.
6. `sprints/06-legacy-cleanup-review-sprint.md` — quarantine/archive stale DCA/seed code from canonical paths, run final acceptance, and publish completion summary.

## Non-goals

- Do not force starter app materialization for global installs or existing projects.
- Do not turn every app-specific domain example into the starter. The starter is the full-core foundation plus extension seams.
- Do not delete legacy examples before their useful mechanics have been inventoried and replacement coverage exists.
- Do not bypass app-description/capability-first semantics; the starter should demonstrate them.

## Done state

The migration is done when a new user can install the pack, explicitly scaffold the starter app into an empty project, run tests/builds, start from a working full-core AI-first SaaS application, and extend it without relying on old DCA/static seed examples as canonical guidance.
